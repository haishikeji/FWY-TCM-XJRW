package com.px.inspection.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.px.common.annotation.DataScope;
import com.px.common.core.domain.TreeSelect;
import com.px.common.core.domain.entity.InspectionArea;
import com.px.common.utils.DateUtils;
import com.px.common.utils.StringUtils;
import com.px.common.utils.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.px.inspection.mapper.InspectionAreaMapper;
import com.px.inspection.service.IInspectionAreaService;

/**
 * 【巡检区域】Service业务层处理
 *
 * @author 品讯科技
 * @date 2024-08
 */
@Service
public class InspectionAreaServiceImpl implements IInspectionAreaService
{
    @Autowired
    private InspectionAreaMapper inspectionAreaMapper;

    /**
     * 查询【巡检区域】
     *
     * @param areaId 【巡检区域】主键
     * @return 【巡检区域】
     */
    @Override
    public InspectionArea selectInspectionAreaByAreaId(Long areaId)
    {
        return inspectionAreaMapper.selectInspectionAreaByAreaId(areaId);
    }

    /**
     * 查询【巡检区域】列表
     *
     * @param inspectionArea 【查询参数】
     * @return 【巡检区域】列表
     */
    @Override
    public List<InspectionArea> selectInspectionAreaList(InspectionArea inspectionArea)
    {
        return inspectionAreaMapper.selectInspectionAreaList(inspectionArea);
    }

    /**
     * 新增【巡检区域】
     *
     * @param inspectionArea 【新增参数】
     * @return 结果
     */
    @Override
    public int insertInspectionArea(InspectionArea inspectionArea)
    {
        if (inspectionArea.getParentId() != null && inspectionArea.getParentId() > 0){
            InspectionArea info = inspectionAreaMapper.selectAreaById(inspectionArea.getParentId());
            if (info != null){
                inspectionArea.setAncestors(info.getAncestors() + "," + inspectionArea.getParentId());
            }
        }else{
            inspectionArea.setAncestors("0");
        }

        inspectionArea.setCreateTime(DateUtils.getNowDate());
        return inspectionAreaMapper.insertInspectionArea(inspectionArea);
    }

    /**
     * 修改【巡检区域】
     *
     * @param inspectionArea 【修改参数】
     * @return 结果
     */
    @Override
    public int updateInspectionArea(InspectionArea inspectionArea)
    {
        inspectionArea.setUpdateTime(DateUtils.getNowDate());
        InspectionArea newParent = inspectionAreaMapper.selectAreaById(inspectionArea.getParentId());
        InspectionArea old = inspectionAreaMapper.selectAreaById(inspectionArea.getAreaId());
        if (StringUtils.isNotNull(newParent) && StringUtils.isNotNull(old))
        {
            String newAncestors = newParent.getAncestors() + "," + newParent.getAreaId();
            String oldAncestors = old.getAncestors();
            inspectionArea.setAncestors(newAncestors);
            updateAreaChildren(inspectionArea.getAreaId(), newAncestors, oldAncestors);
        }
        return inspectionAreaMapper.updateInspectionArea(inspectionArea);
    }
    /**
     * 修改子元素关系
     *
     * @param areaId 被修改的区域ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateAreaChildren(Long areaId, String newAncestors, String oldAncestors)
    {
        List<InspectionArea> children = inspectionAreaMapper.selectChildrenAreaById(areaId);
        for (InspectionArea child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            inspectionAreaMapper.updateAreaChildren(children);
        }
    }

    /**
     * 批量删除【巡检区域】
     *
     * @param areaIds 需要删除的【巡检区域】主键
     * @return 结果
     */
    @Override
    public int deleteInspectionAreaByAreaIds(Long[] areaIds)
    {
        return inspectionAreaMapper.deleteInspectionAreaByAreaIds(areaIds);
    }

    /**
     * 删除【巡检区域】信息
     *
     * @param areaId 【巡检区域】主键
     * @return 结果
     */
    @Override
    public int deleteInspectionAreaByAreaId(Long areaId)
    {
        return inspectionAreaMapper.deleteInspectionAreaByAreaId(areaId);
    }

    /**
     * 是否存在子节点
     *
     * @param areaId ID
     * @return 结果
     */
    @Override
    public boolean hasChildByAreaId(Long areaId)
    {
        int result = inspectionAreaMapper.hasChildByAreaId(areaId);
        return result > 0;
    }



    /**
     * 查询巡检区域管理数据
     *
     * @param area 巡检区域信息
     * @return 巡检区域信息集合
     */
    @Override
    @DataScope(areaAlias = "a")
    public List<InspectionArea> selectAreaList(InspectionArea area)
    {
        return inspectionAreaMapper.selectInspectionAreaList(area);
    }

    /**
     * 查询巡检区域树结构信息
     *
     * @param area 巡检区域信息
     * @return 巡检区域树信息集合
     */
    @Override
    public List<TreeSelect> selectAreaTreeList(InspectionArea area)
    {
        List<InspectionArea> areas = SpringUtils.getAopProxy(this).selectAreaList(area);
        return buildAreaTreeSelect(areas);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param areas 巡检区域列表
     * @return 树结构列表
     */
    @Override
    public List<InspectionArea> buildAreaTree(List<InspectionArea> areas)
    {
        List<InspectionArea> returnList = new ArrayList<InspectionArea>();
        InspectionArea all = new InspectionArea();
        all.setAreaId(Long.parseLong("-1"));
        all.setParentId(Long.parseLong("-1"));
        all.setAreaName("全部");
        returnList.add(all);
        List<Long> tempList = areas.stream().map(InspectionArea::getAreaId).collect(Collectors.toList());
        for (InspectionArea area : areas)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(area.getParentId()))
            {
                recursionFn(areas, area);
                returnList.add(area);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = areas;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param areas 巡检区域列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildAreaTreeSelect(List<InspectionArea> areas)
    {
        List<InspectionArea> areaTrees = buildAreaTree(areas);
        return areaTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<InspectionArea> list, InspectionArea t)
    {
        // 得到子节点列表
        List<InspectionArea> childList = getChildList(list, t);
        t.setChildren(childList);
        for (InspectionArea tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<InspectionArea> getChildList(List<InspectionArea> list, InspectionArea t)
    {
        List<InspectionArea> tlist = new ArrayList<InspectionArea>();
        Iterator<InspectionArea> it = list.iterator();
        while (it.hasNext())
        {
            InspectionArea n = (InspectionArea) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getAreaId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<InspectionArea> list, InspectionArea t)
    {
        return getChildList(list, t).size() > 0;
    }
}
