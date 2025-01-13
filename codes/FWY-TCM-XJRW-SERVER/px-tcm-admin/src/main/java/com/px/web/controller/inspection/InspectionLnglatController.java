package com.px.web.controller.inspection;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.px.common.annotation.Log;
import com.px.common.core.controller.BaseController;
import com.px.common.core.domain.AjaxResult;
import com.px.common.enums.BusinessType;
import com.px.inspection.domain.InspectionLnglat;
import com.px.inspection.service.IInspectionLnglatService;
import com.px.common.utils.poi.ExcelUtil;
import com.px.common.core.page.TableDataInfo;

/**
 * 【巡检点定位】Controller
 *
 * @author 品讯科技
 * @date 2024-08
 */
@RestController
@RequestMapping("/inspection/lnglat")
public class InspectionLnglatController extends BaseController
{
    @Autowired
    private IInspectionLnglatService inspectionLnglatService;

    /**
     * 查询【巡检点定位】列表
     */
    @PreAuthorize("@ss.hasPermi('inspection:lnglat:list')")
    @GetMapping("/list")
    public TableDataInfo list(InspectionLnglat inspectionLnglat)
    {
        startPage();
        List<InspectionLnglat> list = inspectionLnglatService.selectInspectionLnglatList(inspectionLnglat);
        return getDataTable(list);
    }

    /**
     * 导出【巡检点定位】列表
     */
    @PreAuthorize("@ss.hasPermi('inspection:lnglat:export')")
    @Log(title = "导出【巡检点定位】列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InspectionLnglat inspectionLnglat)
    {
        List<InspectionLnglat> list = inspectionLnglatService.selectInspectionLnglatList(inspectionLnglat);
        ExcelUtil<InspectionLnglat> util = new ExcelUtil<InspectionLnglat>(InspectionLnglat.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取【巡检点定位】详细信息
     */
    @PreAuthorize("@ss.hasPermi('inspection:lnglat:query')")
    @GetMapping(value = "/{lnglatId}")
    public AjaxResult getInfo(@PathVariable("lnglatId") Long lnglatId)
    {
        return success(inspectionLnglatService.selectInspectionLnglatByLnglatId(lnglatId));
    }

    /**
     * 新增【巡检点定位】
     */
    @PreAuthorize("@ss.hasPermi('inspection:lnglat:add')")
    @Log(title = "新增【巡检点定位】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InspectionLnglat inspectionLnglat)
    {
        return toAjax(inspectionLnglatService.insertInspectionLnglat(inspectionLnglat));
    }

    /**
     * 修改【巡检点定位】
     */
    @PreAuthorize("@ss.hasPermi('inspection:lnglat:edit')")
    @Log(title = "修改【巡检点定位】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InspectionLnglat inspectionLnglat)
    {
        return toAjax(inspectionLnglatService.updateInspectionLnglat(inspectionLnglat));
    }

    /**
     * 删除【巡检点定位】
     */
    @PreAuthorize("@ss.hasPermi('inspection:lnglat:remove')")
    @Log(title = "删除【巡检点定位】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{lnglatIds}")
    public AjaxResult remove(@PathVariable Long[] lnglatIds)
    {
        return toAjax(inspectionLnglatService.deleteInspectionLnglatByLnglatIds(lnglatIds));
    }
}
