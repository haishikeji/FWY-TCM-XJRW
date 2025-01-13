<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" width="500px" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="Id" prop="projectId">
        <el-input
          v-model="queryParams.projectId"
          placeholder="Id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="巡检计划" prop="planId">
        <el-select v-model="queryParams.planId" clearable placeholder="请选择巡检计划" style="width: 100%">
          <el-option
            v-for="item in planOptions"
            :key="item.planId"
            :label="item.planName"
            :value="item.planId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="巡检线路" prop="routeId">
        <el-select v-model="queryParams.routeId" clearable placeholder="请选择巡检线路" style="width: 100%">
          <el-option
            v-for="item in routeOptions"
            :key="item.routeId"
            :label="item.routeName"
            :value="item.routeId"
          ></el-option>
        </el-select>
      </el-form-item>
<!--      <el-form-item label="巡检区域" prop="areaId">-->
<!--        <treeselect v-model="queryParams.areaId" style="width: 200px;height:100%;"-->
<!--                    :options="areaOptions" :normalizer="normalizer" placeholder="请选择巡检区域" />-->
<!--      </el-form-item>-->
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="-"
          value-format="yyyy-MM-dd HH:mm:ss"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']">
          align="right">
        </el-date-picker>
      </el-form-item>
<!--      <el-form-item label="巡检结果" prop="state">-->
<!--        <el-select v-model="queryParams.state" clearable placeholder="请选择巡检结果" style="width: 100%">-->
<!--          <el-option-->
<!--            v-for="dict in dict.type.inspection_project_results"-->
<!--            :key="parseInt(dict.value)"-->
<!--            :label="dict.label"-->
<!--            :value="parseInt(dict.value)"-->
<!--          ></el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="跳检原因" prop="jumpReason">-->
<!--        <el-input-->
<!--          v-model="queryParams.jumpReason"-->
<!--          placeholder="请输入跳检原因"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="巡检点" prop="areaSiteId">-->
<!--        <el-select v-model="queryParams.areaSiteId" clearable placeholder="请选择巡检点" style="width: 100%">-->
<!--          <el-option-->
<!--            v-for="item in areaSiteOptions"-->
<!--            :key="item.siteId"-->
<!--            :label="item.siteName"-->
<!--            :value="item.siteId"-->
<!--          ></el-option>-->
<!--        </el-select>-->
<!--      </el-form-item>-->
      <el-form-item label="开始时间" prop="beginTime">
        <el-date-picker
          v-model="dateRangeBegin"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="-"
          value-format="yyyy-MM-dd HH:mm:ss"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']">
          align="right">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="dateRangeEnd"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="-"
          value-format="yyyy-MM-dd HH:mm:ss"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']">
          align="right">
        </el-date-picker>
      </el-form-item>
<!--      <el-form-item label="检查时间" prop="checkTime">-->
<!--        <el-date-picker-->
<!--          v-model="dateRangeCheck"-->
<!--          type="datetimerange"-->
<!--          :picker-options="pickerOptions"-->
<!--          range-separator="-"-->
<!--          value-format="yyyy-MM-dd HH:mm:ss"-->
<!--          start-placeholder="开始日期"-->
<!--          end-placeholder="结束日期"-->
<!--          :default-time="['00:00:00', '23:59:59']">-->
<!--          align="right">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
<!--      <el-form-item label="跳检时间" prop="jumpTime">-->
<!--        <el-date-picker-->
<!--          v-model="dateRangeJump"-->
<!--          type="datetimerange"-->
<!--          :picker-options="pickerOptions"-->
<!--          range-separator="-"-->
<!--          value-format="yyyy-MM-dd HH:mm:ss"-->
<!--          start-placeholder="开始日期"-->
<!--          end-placeholder="结束日期"-->
<!--          :default-time="['00:00:00', '23:59:59']">-->
<!--          align="right">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->

      <el-form-item label="巡检进度" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="请选择巡检进度" style="width: 100%">
          <el-option
            v-for="dict in dict.type.inspection_project_progress"
            :key="parseInt(dict.value)"
            :label="dict.label"
            :value="parseInt(dict.value)"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="info"
          icon="el-icon-refresh"
          size="mini"
          @click="getList"
          v-hasPermi="['inspection:project:list']"
        ></el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-bangzhu"
          size="mini"
          @click="handleGenerate"
          v-hasPermi="['inspection:project:add']"
        >生成巡检任务</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['inspection:project:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['inspection:project:export']"
        >导出</el-button>
      </el-col>

<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          size="mini"-->
<!--          @click="getTest()"-->
<!--        >测试</el-button>-->
<!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="Id" align="center" prop="projectId" />
      <el-table-column label="巡检计划" align="center" prop="plan.planName" width="180" />
      <el-table-column label="巡检线路" align="center" prop="route.routeName" />
<!--      <el-table-column label="巡检区域" align="center" prop="area.areaName" />-->
      <el-table-column label="开始时间" align="center" prop="beginTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.beginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
<!--      <el-table-column label="检查时间" align="center" prop="checkTime" width="150">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.checkTime) }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="跳检时间" align="center" prop="jumpTime" width="150">-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{ parseTime(scope.row.jumpTime) }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
<!--      <el-table-column label="跳检原因" align="center" prop="jumpReason" />-->
<!--      <el-table-column label="图片" align="center" prop="images" />-->
<!--      <el-table-column label="备注" align="center" prop="remarks" />-->
      <el-table-column label="巡检进度" align="center" prop="status">
      <template slot-scope="scope">
          <span>{{scope.row.status == -1?"漏检": scope.row.status == 0? '进行中' : scope.row.status == 1? '已巡检' : '跳检'}}</span>
        </template>
      </el-table-column>
<!--      <el-table-column label="巡检结果" align="center" prop="state" >-->
<!--        <template slot-scope="scope">-->
<!--          <span>{{scope.row.state == 0 || scope.row.state == null ?"": scope.row.state == 1? '正常' : scope.row.state == 2? '异常' : ''}}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="warning"
            @click="handleProjectSites(scope.row)"
            v-hasPermi="['inspection:project:list']"
          >巡检点详情</el-button>
          <el-button
            size="mini"
            type="danger"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['inspection:project:remove']"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="巡检计划ID" prop="planId">
          <el-input v-model="form.planId" placeholder="请输入巡检计划ID" />
        </el-form-item>
        <el-form-item label="巡检线路ID" prop="routeId">
          <el-input v-model="form.routeId" placeholder="请输入巡检线路ID" />
        </el-form-item>
        <el-form-item label="巡检区域ID" prop="areaId">
          <el-input v-model="form.areaId" placeholder="请输入巡检区域ID" />
        </el-form-item>
        <el-form-item label="巡检点ID" prop="areaSiteId">
          <el-input v-model="form.areaSiteId" placeholder="请输入巡检点ID" />
        </el-form-item>
        <el-form-item label="开始时间" prop="beginTime">
          <el-date-picker clearable
            v-model="form.beginTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable
            v-model="form.endTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择结束时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="巡检人ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入巡检人ID" />
        </el-form-item>
        <el-form-item label="检查时间" prop="checkTime">
          <el-date-picker clearable
            v-model="form.checkTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择检查时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="跳检时间" prop="jumpTime">
          <el-date-picker clearable
            v-model="form.jumpTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择跳检时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="跳检原因" prop="jumpReason">
          <el-input v-model="form.jumpReason" placeholder="请输入跳检原因" />
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <el-input v-model="form.images" placeholder="请输入图片" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="状态(0进行中 1正常 2异常)" prop="state">
          <el-input v-model="form.state" placeholder="请输入状态(0进行中 1正常 2异常)" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!--  巡检点列表详情  -->
    <el-dialog :title="siteTitle" :visible.sync="siteOpen" width="800px" append-to-body>
      <el-table v-loading="siteLoading" :data="projectSiteList">
        <el-table-column label="巡检区域" align="center" prop="areaName" />
        <el-table-column label="巡检点名称" align="center" prop="siteName" />
        <el-table-column label="备注" align="center" prop="remarks" />
        <el-table-column label="检查结果" align="center" prop="state">
          <template slot-scope="scope">
            <span>{{scope.row.state  == null ?"未巡检":(scope.row.state == 1?"正常":"异常")}}</span>
          </template>
        </el-table-column>
        <el-table-column label="巡检时间" align="center" prop="checkTime" width="150">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.checkTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <span>{{scope.row.status == -1?"漏检": scope.row.status == 0? '进行中' : scope.row.status == 1? '已巡检' : '跳检'}}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status == 1"
              size="mini"
              type="warning"
              @click="handleDetails(scope.row)"
              v-hasPermi="['inspection:project:list']"
            >检查项记录</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="siteTotal>0"
        :total="siteTotal"
        :page.sync="querySiteParams.pageNum"
        :limit.sync="querySiteParams.pageSize"
        @pagination="getList"
      />
    </el-dialog>


    <!--  巡检详情  -->
    <el-dialog :title="itemTitle" :visible.sync="itemOpen" width="800px" append-to-body>
      <el-table v-loading="itemLoading" :data="projectItemList" @selection-change="handleSelectionChange">
        <el-table-column label="检查项" align="center" prop="itemName" />
        <el-table-column label="检查结果" align="center" prop="value">
          <template slot-scope="scope">
<!--            <span>{{scope.row.value == 1?"正常":"异常"}}</span>-->
            <span>{{scope.row.type == 1?(scope.row.value == 0?"正常":"异常"):scope.row.value}}</span>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="itemTotal>0"
        :total="itemTotal"
        :page.sync="queryItemParams.pageNum"
        :limit.sync="queryItemParams.pageSize"
        @pagination="getList"
      />
    </el-dialog>
  </div>
</template>

<script>
import { listProject, getProject, delProject, addProject, updateProject,generateProject,
  checkLngLat,getProjectListByUserId,getInspectionRecords,getInspectionRecordSites,getInspectionRecordInfo,getRouteSiteList, getAreaItemList,commitProject
} from "@/api/inspection/project";
import { allRoute } from "@/api/inspection/route";
import { allPlan } from "@/api/inspection/plan";
import { allAreaSite } from "@/api/inspection/areaSite";
import { listAreaTree, listArea, getArea, delArea, addArea, updateArea, areaTreeSelect } from "@/api/inspection/area";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import { listProjectItem } from "@/api/inspection/projectItem";
import dateModule from "@/utils/date"
import { listProjectSite } from "@/api/inspection/projectSite";

export default {
  name: "Project",
  dicts: ['inspection_project_progress','inspection_project_results'],
  components: { Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 【请填写功能名称】表格数据
      projectList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        planId: null,
        routeId: null,
        areaId: null,
        areaSiteId: null,
        beginTime: null,
        endTime: null,
        userId: null,
        checkTime: null,
        jumpTime: null,
        jumpReason: null,
        images: null,
        remarks: null,
        status: null,
        state: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 巡检线路选项
      routeOptions: [],
      // 巡检计划选项
      planOptions: [],
      // 巡检点选项
      areaSiteOptions: [],
      // 巡检区域树选项
      areaOptions: [],

      //巡检详情
      // 弹出层标题
      itemTitle: "",
      // 是否显示弹出层
      itemOpen: false,
      // 遮罩层
      itemLoading: false,
      // 【巡检详情】表格数据
      projectItemList: [],
      // 总条数
      itemTotal: 0,
      // 查询参数
      queryItemParams: {
        pageNum: 1,
        pageSize: 10,
        projectId:null,
        projectSiteId:null,
      },
      pickerOptions: {
        shortcuts: [
          {
            text: '今天',
            onClick(picker) {
              picker.$emit('pick', [ new Date(dateModule.today()[0]),new Date(dateModule.today()[1])]);
            }
          },{
            text: '昨天',
            onClick(picker) {
              picker.$emit('pick', [ new Date(dateModule.yesterday()[0]),new Date(dateModule.yesterday()[1])]);
            }
          },{
            text: '最近7天',
            onClick(picker) {
              picker.$emit('pick', [ new Date(dateModule.sevenDays()[0]),new Date(dateModule.sevenDays()[1])]);
            }
          },{
            text: '最近30天',
            onClick(picker) {
              picker.$emit('pick', [ new Date(dateModule.thirtyDays()[0]),new Date(dateModule.thirtyDays()[1])]);
            }
          },{
            text: '本月',
            onClick(picker) {
              picker.$emit('pick', [ new Date(dateModule.getCurrentMonth()[0]),new Date(dateModule.getCurrentMonth()[1])]);
            }
          }, {
            text: '上月',
            onClick(picker) {
              picker.$emit('pick', [ new Date(dateModule.getLastMonth()[0]),new Date(dateModule.getLastMonth()[1])]);
            }
          }]
      },
      // 日期范围
      dateRangeBegin: [],
      dateRangeEnd: [],
      dateRangeCheck: [],
      dateRangeJump: [],
      dateRange: [],

      querySiteParams:{
        pageNum: 1,
        pageSize: 10,
        projectId:null,
      },
      //巡检点列表
      projectSiteList: [],
      siteTotal:0,
      // 弹出层标题
      siteTitle: "",
      // 是否显示弹出层
      siteOpen: false,
      // 遮罩层
      siteLoading: false,
    };
  },
  created() {
    this.getList();

    this.getAllRoute();
    this.getAllPlan();
    this.getAllAreaSite();
    this.getAllArea();
  },
  methods: {
    getTest() {
      // 判断经纬度是否在范围之内
      // import { checkLngLat } from "@/api/inspection/project";
      // var param = {
      //   projectId:1,
      //   userId:1,
      //   lng:112.982279,
      //   lat:28.19409
      // };
      // checkLngLat(
      //   param
      // ).then(response => {
      //   console.log(response)
      // });

      //获取巡检线路列表
      //import { getInspectionPlanListForRoute} from "@/api/inspection/plan";
      // var param1 = {
      //   pageNum: 1,
      //   pageSize: 10,
      //   userId:1,
      // };
      // getInspectionPlanListForRoute(
      //   param1
      // ).then(response => {
      //   console.log(response)
      // });

      //获取登录用户的巡检任务列表
      //import { getProjectListByPlanId } from "@/api/inspection/project";
      var param2 = {
        pageNum: 1,
        pageSize: 10,
        userId: 1,//用户编号
        status: 0,//状态
        beginTime: '2023-06-21 00:00:00',//开始时间
        endTime: '2023-06-21 23:59:59'//结束时间
      }
      getProjectListByUserId(
        param2
      ).then(response => {
        console.log(response)
      });

      var param6 = {
        projectId:7,
        userId: 1,//用户编号
      }
      getRouteSiteList(
        param6
      ).then(response => {
        console.log(response)
      });

      var param7 = {
        projectId:7,
        areaSiteId:1,
        userId: 1,//用户编号
      }
      getAreaItemList(
        param7
      ).then(response => {
        console.log(response)
      });

      // var param5 = {
      //   projectId:7,
      //   projectSiteId:1
      // };
      // getInspectionRecordInfo(
      //   param5
      // ).then(response => {
      //   console.log(response)
      // });

      //获取巡检点的检查项列表
      //import { getProjectListByPlanId } from "@/api/inspection/project";
      // var param6 = {
      //   projectId:7,
      //   areaSiteId:1,
      //   userId: 1,//用户编号
      // }
      // getAreaItemList(
      //   param6
      // ).then(response => {
      //   console.log(response)
      // });

      // //提交巡检任务(post)
      // //import { commitProject } from "@/api/inspection/project";
      // var items = [
      //   {
      //     areaItemId:1,
      //     itemName:'门禁设备是否正常运行',
      //     type:1,
      //     value:0,
      //   },
      //   {
      //     areaItemId:2,
      //     itemName:'路面卫生是否整洁',
      //     type:0,
      //     value:1,
      //   },
      //   {
      //     areaItemId:3,
      //     itemName:'设备间温度',
      //     type:1,
      //     value:'988度',
      //   },
      // ];
      // var imgs = ['https://image.baidu.com/1','https://image.baidu.com/2','https://image.baidu.com/3'];
      // var projectSite = {
      //   //备注、图片、巡检结果、经度、纬度、
      //   projectId:8,
      //   areaSiteId:3,
      //   state: 1,
      //   remarks: '88888',
      //   imgs: imgs,//图片
      //   projectItemList: items
      // }
      // var model = {
      //   longitude: 112.88029,//需要定位时提交
      //   latitude: 28.22719,//需要定位时提交
      //   projectId: 8,
      //   inspectionProjectSite:projectSite
      // }
      // commitProject(
      //   JSON.stringify(model)
      // ).then(response => {
      //   console.log(response)
      // });

      // //获取巡检记录
      // //import { getInspectionRecords } from "@/api/inspection/project";
      // var param3 = {
      //   pageNum: 1,
      //   pageSize: 10,
      //   userId:1,
      // };
      // getInspectionRecords(
      //   param3
      // ).then(response => {
      //   console.log(response)
      // });
      //
      // //获取巡检记录任务点列表
      // //import { getInspectionRecordSites } from "@/api/inspection/project";
      var param4 = {
        projectId:14
      };
      getInspectionRecordSites(
        param4
      ).then(response => {
        console.log(response)
      });
      //

      //获取巡检任务的巡检点列表
      //import { getProjectListByPlanId } from "@/api/inspection/project";
      // var param6 = {
      //   projectId:8,
      //   userId: 1,//用户编号
      // }
      // getRouteSiteList(
      //   param6
      // ).then(response => {
      //   console.log(response)
      // });
      //获取巡检任务点详情
      //import { getInspectionRecordInfo } from "@/api/inspection/project";
      // var param5 = {
      //   projectId:8,
      //   projectSiteId:13
      // };
      // getInspectionRecordInfo(
      //   param5
      // ).then(response => {
      //   console.log(response)
      // });
    },
    /** 转换数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.areaId,
        label: node.areaName,
        children: node.children
      };
    },
    getAllArea(){
      listAreaTree().then(response =>{
        this.areaOptions = this.handleTree(response.data, "areaId");
      });
    },
    getAllAreaSite(){
      allAreaSite().then(response => {
        this.areaSiteOptions = response.rows;
      });
    },
    getAllRoute(){
      allRoute().then(response => {
        this.routeOptions = response.rows;
      });
    },
    getAllPlan(){
      allPlan().then(response => {
        this.planOptions = response.rows;
      });
    },
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listProject(
        this.addDateRange(this.queryParams, this.dateRangeBegin,'Start'),
        this.addDateRange(this.queryParams, this.dateRangeEnd,'Finish'),
        this.addDateRange(this.queryParams, this.dateRangeCheck,'Check'),
        this.addDateRange(this.queryParams, this.dateRangeJump,'Jump'),
        this.addDateRange(this.queryParams, this.dateRange)
      ).then(response => {
        this.projectList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        projectId: null,
        planId: null,
        routeId: null,
        areaId: null,
        areaSiteId: null,
        beginTime: null,
        endTime: null,
        userId: null,
        checkTime: null,
        jumpTime: null,
        jumpReason: null,
        images: null,
        remarks: null,
        status: null,
        state: null,
        delFlag: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.projectId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加【请填写功能名称】";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const projectId = row.projectId || this.ids
      getProject(projectId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改【请填写功能名称】";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.projectId != null) {
            updateProject(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProject(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const projectIds = row.projectId || this.ids;
      this.$modal.confirm('是否确认删除编号为"' + projectIds + '"的数据项？').then(function() {
        return delProject(projectIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('inspection/project/export', {
        ...this.queryParams
      }, `巡检任务_${new Date().getTime()}.xlsx`)
    },
    handleGenerate(){
      generateProject().then(response => {
        this.$modal.msgSuccess("生成成功");
        this.getList();
      });
    },
    /** 巡检点详情按钮操作 */
    handleProjectSites(row) {
      this.siteOpen = true;
      this.siteTitle = "巡检点详情";
      this.querySiteParams.projectId = row.projectId;
      this.getSiteList();
    },
    /** 查询巡检点列表 */
    getSiteList() {
      this.siteLoading = true;
      listProjectSite(this.querySiteParams).then(response => {
        this.projectSiteList = response.rows;
        this.siteTotal = response.total;
        this.siteLoading = false;
      });
    },
    /** 巡检详情按钮操作 */
    handleDetails(row) {
      console.log(row);
      this.itemOpen = true;
      this.itemTitle = "检查项记录";
      this.queryItemParams.projectId = row.projectId;
      this.queryItemParams.projectSiteId = row.siteId;
      this.getItemList();
    },
    /** 查询【巡检任务详情】列表 */
    getItemList() {
      this.itemLoading = true;
      listProjectItem(this.queryItemParams).then(response => {
        this.projectItemList = response.rows;
        this.itemTotal = response.total;
        this.itemLoading = false;
      });
    },
  }
};
</script>
