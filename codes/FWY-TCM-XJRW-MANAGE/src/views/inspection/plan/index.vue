<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="108px">
      <el-form-item label="Id" prop="planId">
        <el-input
          v-model="queryParams.planId"
          placeholder="Id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="巡检计划名称" prop="planName">
        <el-input
          v-model="queryParams.planName"
          placeholder="请输入巡检计划名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
      <el-form-item label="开始时间" prop="dateRangeBegin">
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
      <el-form-item label="结束时间" prop="dateRangeEnd">
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
      <el-form-item label="添加时间" prop="dateRange">
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['inspection:plan:add']"
        >添加</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['inspection:plan:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['inspection:plan:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['inspection:plan:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="planList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="Id" align="center" prop="planId" />
      <el-table-column label="巡检计划名称" align="center" prop="planName" />
      <el-table-column label="巡检线路" align="center" prop="route.routeName" />
      <el-table-column label="计划类型" align="center" prop="circleType">
        <template slot-scope="scope">
          <span v-if="scope.row.circleType == 1">日计划</span>
          <span v-else-if="scope.row.circleType == 2">周计划</span>
          <span v-else="scope.row.circleType == 3">月计划</span>

        </template>
      </el-table-column>
      <el-table-column label="执行条件" align="center" prop="circleValue" />
      <el-table-column label="周期内执行次数" align="center" prop="num" />
      <el-table-column label="每次间隔时长(分钟)" align="center" prop="time" width="150"/>
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
      <el-table-column label="创建时间" align="center" prop="createTime" width="150">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['inspection:plan:edit']"
          ></el-button>
          <el-button
            size="mini"
            type="danger"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['inspection:plan:remove']"
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

    <!-- 添加或修改【巡检计划】对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="巡检计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入巡检计划名称" />
        </el-form-item>
        <el-form-item label="巡检线路ID" prop="routeId">
<!--          <el-input v-model="form.routeId" placeholder="请输入巡检线路ID" />-->
          <el-select v-model="form.routeId" placeholder="请选择巡检线路" style="width: 100%">
            <el-option
              v-for="item in routeOptions"
              :key="item.routeId"
              :label="item.routeName"
              :value="item.routeId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="计划类型" prop="circleType">
          <el-select v-model="form.circleType" placeholder="请选择计划类型" style="width: 100%">
            <el-option
              v-for="dict in dict.type.inspection_plan_type"
              :key="parseInt(dict.value)"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="" prop="circleValue" v-show="form.circleType && form.circleType != 1">
          <div v-if="form.circleType == 2">
            <el-checkbox-group v-model="form.circleValue">
              <el-checkbox v-for="dict in dict.type.inspection_circle_week"
                           :key="dict.value"
                           :label="dict.value"
              >{{dict.label}}</el-checkbox>
            </el-checkbox-group>

          </div>
          <div v-else-if="form.circleType == 3">
            <el-select v-model="form.circleValue" multiple placeholder="请选择计划类型" style="width: 100%">
              <el-option
                v-for="dict in dict.type.inspection_circle_month"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </div>
          <div v-else>

          </div>
        </el-form-item>
        <el-form-item label="周期内执行次数" prop="num">
<!--          <el-input v-model="form.num" placeholder="请输入周期内执行次数" />-->
          <el-input-number
            style="width: 100%"
            v-model="form.num"
            :min="0"
            :precision="0"
            :step="1"
            controls-position="right"
            placeholder="请输入周期内执行次数" />
        </el-form-item>
        <el-form-item label="每次间隔时长(分钟)" prop="time">
<!--          <el-input v-model="form.time" placeholder="请输入每次间隔时长" />-->
          <el-input-number
            style="width: 100%"
            v-model="form.time"
            :min="0"
            :precision="0"
            :step="1"
            controls-position="right"
            placeholder="请输入每次间隔时长" />
        </el-form-item>
        <el-form-item label="开始时间" prop="beginTime">
          <el-date-picker clearable style="width: 100%"
            v-model="form.beginTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker clearable style="width: 100%"
            v-model="form.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="如果永久有效，请留空">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="巡检人" prop="userName">
<!--          <el-input v-model="form.userId" placeholder="请选择巡检人" />-->
          <el-row>
            <el-col :span="20">
              <el-input readonly="readonly" :value="planUser.userName"></el-input>
            </el-col>
            <el-col :span="3" class="ml5">
              <el-button type="danger" icon="el-icon-s-custom"
                         @click="handleChoseUser()"
                         style="padding: 6px 8px;font-size: 18px;font-weight: bold"></el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 选择巡检人对话框 -->
    <el-dialog title="选择巡检人员" :visible.sync="openUser" width="1200px" append-to-body>
      <div>
        <el-row :gutter="20">
          <!--区域数据-->
          <el-col :span="4" :xs="24">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>部门列表</span>
              </div>
              <div class="head-container">
                <el-button type="primary" icon="el-icon-refresh" size="small" @click="handleRefresh"></el-button>
              </div>
              <div class="head-container">
                <el-tree
                  :data="deptOptions"
                  :props="defaultProps"
                  :expand-on-click-node="false"
                  :filter-node-method="filterNode"
                  ref="tree"
                  node-key="id"
                  default-expand-all
                  highlight-current
                  @node-click="handleNodeClick"
                >
                  <span slot-scope="{ node }" style="font-size: 13px; color: #000">
                    <i style="margin-right: 5px" class="el-icon-folder" />
                    <span>{{ node.label }}</span>
                  </span>
                </el-tree>
              </div>
            </el-card>
          </el-col>
          <!--巡检点数据-->
          <el-col :span="20" :xs="24">
            <!--        style="max-height: 700px;overflow-y: scroll;"-->
            <div>
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span>员工列表</span>
                </div>
                <div>
                  <el-form :model="queryUserParams" ref="queryUserForm" size="small" :inline="true" v-show="showUserSearch" label-width="68px">
                    <el-form-item label="用户名称" prop="userName">
                      <el-input
                        v-model="queryUserParams.userName"
                        placeholder="请输入用户名称"
                        clearable
                        style="width: 240px"
                        @keyup.enter.native="handleQuery"
                      />
                    </el-form-item>
                    <el-form-item label="手机号码" prop="phonenumber">
                      <el-input
                        v-model="queryUserParams.phonenumber"
                        placeholder="请输入手机号码"
                        clearable
                        style="width: 240px"
                        @keyup.enter.native="handleQuery"
                      />
                    </el-form-item>
                    <el-form-item label="创建时间">
                      <el-date-picker
                        v-model="dateRange"
                        style="width: 240px"
                        value-format="yyyy-MM-dd"
                        type="daterange"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                      ></el-date-picker>
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" icon="el-icon-search" size="mini" @click="handleUserQuery">搜索</el-button>
                      <el-button icon="el-icon-refresh" size="mini" @click="resetUserQuery">重置</el-button>
                    </el-form-item>
                  </el-form>

                  <el-row :gutter="10" class="mb8">
                    <right-toolbar :showSearch.sync="showUserSearch" @queryTable="getUserList" :columns="userColumns"></right-toolbar>
                  </el-row>

                  <el-table v-loading="userLoading" :data="userList">
                    <el-table-column label="操作" width="90" align="center" header-align="center">
                      <template slot-scope="scope">
                        <el-button size="mini" type="danger" @click="handleSelectUser(scope.row)">
                          选择
                        </el-button>
                      </template>
                    </el-table-column>
                    <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="userColumns[0].visible" />
                    <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="userColumns[1].visible" :show-overflow-tooltip="true" />
                    <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="userColumns[2].visible" :show-overflow-tooltip="true" />
                    <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" v-if="userColumns[3].visible" :show-overflow-tooltip="true" />
                    <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="userColumns[4].visible" width="120" />
                    <el-table-column label="状态" align="center" key="status" v-if="userColumns[5].visible">
                      <template slot-scope="scope">
                        <el-switch
                          disabled
                          v-model="scope.row.status"
                          active-value="0"
                          inactive-value="1"
                        ></el-switch>
<!--                        @change="handleStatusChange(scope.row)"-->
                      </template>
                    </el-table-column>
                    <el-table-column label="创建时间" align="center" prop="createTime" v-if="userColumns[6].visible" width="160">
                      <template slot-scope="scope">
                        <span>{{ parseTime(scope.row.createTime) }}</span>
                      </template>
                    </el-table-column>
                  </el-table>

                  <pagination
                    v-show="userTotal>0"
                    :total="userTotal"
                    :page.sync="queryUserParams.pageNum"
                    :limit.sync="queryUserParams.pageSize"
                    @pagination="getUserList"
                  />
                </div>
              </el-card>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPlan, getPlan, delPlan, addPlan, updatePlan } from "@/api/inspection/plan";
import { listUser, getUser, delUser, addUser, updateUser, resetUserPwd, changeUserStatus, deptTreeSelect } from "@/api/system/user";
import Treeselect from "@riophae/vue-treeselect";
import { getDicts as getDicts } from '@/api/system/dict/data'
import { allRoute } from "@/api/inspection/route";
import dateModule from "@/utils/date"

export default {
  name: "Plan",
  dicts: ['inspection_plan_type','inspection_circle_week','inspection_circle_month'],
  components: { Treeselect },
  data() {
    const checkPlanName = (rule, value, callback) => {
      if (!value || value === '' || value.trim().length == 0) {
        callback(new Error("计划名称不能为空"));
      } else {
        callback();
      }
    };
    let checkUserName = (rule, value, callback) => {
      console.log(value);
      console.log(rule);
      console.log(this.planUser.userName);
        if (!this.planUser.userName || !this.planUser.userId) {
          return callback(new Error('巡检人不能为空'));
        }else{
          callback()
        }
      };

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
      planList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        routeId: null,
        planName: null,
        circleType: null,
        circleValue: null,
        num: null,
        time: null,
        beginTime: null,
        endTime: null,
        userId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        planName:[
          { required: true, message: "请输入计划名称", trigger: "blur" },
          { required: true, validator: checkPlanName, trigger: "blur" }
        ],
        routeId:[
          { required: true, message: "请选择巡检线路", trigger: "blur" },
        ],
        circleType:[
          { required: true, message: "请选择计划类型", trigger: "blur" },
        ],
        num:[
          { required: true, message: "请输入周期内执行次数", trigger: "blur" },
        ],
        time:[
          { required: true, message: "请输入每次间隔时长", trigger: "blur" },
        ],
        beginTime:[
          { required: true, message: "请输入开始时间", trigger: "blur" },
        ],
        userId:[
          { required: true, message: "请选择巡检人", trigger: "blur" },
        ],
        userName:[
          {
            required: true,
            validator: checkUserName,
            trigger: 'blur'
          }
        ]
      },
      // 巡检线路选项
      routeOptions: [],
      openUser:false,
      // 部门树选项
      deptOptions: [],
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 查询参数
      queryUserParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        deptId: undefined
      },
      // 列信息
      userColumns: [
        { key: 0, label: `用户编号`, visible: true },
        { key: 1, label: `用户名称`, visible: true },
        { key: 2, label: `用户昵称`, visible: true },
        { key: 3, label: `部门`, visible: true },
        { key: 4, label: `手机号码`, visible: true },
        { key: 5, label: `状态`, visible: true },
        { key: 6, label: `创建时间`, visible: true }
      ],
      // 总条数
      userTotal: 0,
      // 用户表格数据
      userList: null,
      // 遮罩层
      userLoading: false,
      // 显示搜索条件
      showUserSearch: true,
      planUser:{

      },
      circleWeek:[],
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
      dateRange: [],
    };
  },
  created() {
    this.getList();

    this.getAllRoute();
  },
  methods: {
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listPlan(
        this.addDateRange(this.queryParams, this.dateRangeBegin,'Start'),
        this.addDateRange(this.queryParams, this.dateRangeEnd,'Finish'),
        this.addDateRange(this.queryParams, this.dateRange)
      ).then(response => {
        this.planList = response.rows;
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
        planId: null,
        routeId: null,
        planName: null,
        circleType: null,
        circleValue: [],
        num: null,
        time: null,
        beginTime: null,
        endTime: null,
        userId: null,
        delFlag: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.resetForm("form");
      this.planUser={}
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
      this.ids = selection.map(item => item.planId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      getPlan().then(response => {
        // this.routeOptions = response.routeList;
        this.open = true;
        this.title = "添加";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const planId = row.planId || this.ids
      getPlan(planId).then(response => {
        // this.routeOptions = response.routeList;
        this.form = response.data;
        this.open = true;
        this.title = "修改";
        var userInfo = response.planUserInfo;
        if (userInfo){
          this.planUser = {
            userId : userInfo.userId,
            userName: userInfo.userName,
          }
        }

        if (response.data.circleValue ){
          this.$set(this.form, "circleValue", response.data.circleValue.split(","));
          // this.form.circleValue =  response.data.circleValue.split(",")
        }else{
          this.$set(this.form, "circleValue", []);
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      console.log(this.form)
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.userId = this.planUser.userId
          if (this.form.circleValue){
            this.form.circleValue = this.form.circleValue.join(",")
          }

          if (this.form.planId != null) {
            updatePlan(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPlan(this.form).then(response => {
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
      const planIds = row.planId || this.ids;
      this.$modal.confirm('是否确认删除【请填写功能名称】编号为"' + planIds + '"的数据项？').then(function() {
        return delPlan(planIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('inspection/plan/export', {
        ...this.queryParams
      }, `巡检计划_${new Date().getTime()}.xlsx`)
    },



    /** 查询部门下拉树结构 */
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data;
      });
    },
    /** 刷新部门下拉树结构 */
    handleRefresh(){
      this.areaDatas = [];
      this.getDeptTree();
    },
    // 筛选节点
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    // 节点单击事件
    handleNodeClick(data) {
      this.queryUserParams.deptId = data.id;
      this.handleUserQuery();
    },
    /** 搜索按钮操作 */
    handleUserQuery() {
      this.queryUserParams.pageNum = 1;
      this.getUserList();
    },
    /** 重置按钮操作 */
    resetUserQuery() {
      this.dateRange = [];
      this.resetForm("queryUserForm");
      this.queryUserParams.deptId = undefined;
      this.$refs.tree.setCurrentKey(null);
      this.handleUserQuery();
    },
    /** 查询用户列表 */
    getUserList() {
      this.userLoading = true;
      listUser(this.addDateRange(this.queryUserParams, this.dateRange)).then(response => {
          this.userList = response.rows;
          this.userTotal = response.total;
          this.userLoading = false;
        }
      );
    },
    /** 选择巡检人员 */
    handleSelectUser(row){
      console.log(row)
      this.openUser = false;
      var item = {
        userId : row.userId,
        userName: row.userName,
      }
      this.planUser = item;
    },
    handleChoseUser(){
      this.openUser = true;

      this.getDeptTree();
      this.getUserList();
    },
    handleCheckedWeekChange(item) {
      console.log(item)
      // this.form.circleValue.push(item.value)
    },
    getAllRoute(){
      allRoute().then(response => {
        this.routeOptions = response.rows;
      });
    }
  }
};
</script>
