<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流程名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入流程名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务号" prop="bussinesskey">
        <el-input
          v-model="queryParams.bussinesskey"
          placeholder="请输入业务号"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流程实例编号" align="center" prop="processInstanceId" />
      <el-table-column label="业务号" align="center" prop="businessKey" :show-overflow-tooltip="true" />
      <el-table-column label="流程名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="当前节点" align="center" prop="currentTask" :show-overflow-tooltip="true" />
      <el-table-column label="当前处理人" align="center" prop="assignee" :show-overflow-tooltip="true" />
      <el-table-column label="是否挂起" align="center" prop="suspended">
        <template slot-scope="scope">
          <span v-if="scope.row.suspended == true">是</span>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发起人" align="center" prop="startUserId" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-s-operation"
            @click="handleSchedule(scope.row)"
            v-hasPermi="['work:monitor:traceProcess']"
          ></el-button>
          <el-button
            v-if="scope.row.suspended == true"
            type="primary"
            size="mini"
            icon="el-icon-bell"
            @click="handleRun(scope.row)"
            v-hasPermi="['work:monitor:run']"
          ></el-button>
          <el-button
            v-else
            type="warning"
            size="mini"
            icon="el-icon-close-notification"
            @click="handleSuspend(scope.row)"
            v-hasPermi="['work:monitor:suspend']"
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

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <img :src="proc_img">
    </el-dialog>
  </div>
</template>

<script>
  import { listProcess, getTraceProcess, getProcessSuspend, getProcessRun } from "@/api/work/monitor/flowMonitor";

  export default {
    name: "index",
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
        // 模型表格数据
        list: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: undefined,
          bussinesskey: undefined,
        },

        proc_queryParams:{},
        proc_img:null,
        processInstanceId:null,
      }
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询列表 */
      getList() {
        this.loading = true;
        listProcess(this.queryParams).then(response => {
            console.log(response)
            this.list = response.rows;
            this.total = response.total;
            this.loading = false;
          }
        );
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.dateRange = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      handleDefinition() {

      },
      handleFlowChart() {

      },
      handleSchedule(row){
        this.open = true;
        this.proc_queryParams.processInstanceId = row.processInstanceId
        this.getTraceProcess();
      },
      handleSuspend(row){
        getProcessSuspend(row.processInstanceId).then(response => {
          this.$modal.msgSuccess("挂起成功");
          this.getList();
        });
      },
      handleRun(row){
        getProcessRun(row.processInstanceId).then(response => {
          this.$modal.msgSuccess("唤醒成功");
          this.getList();
        });
      },
      getTraceProcess() {
        getTraceProcess(this.proc_queryParams).then(response => {
          // const myBlob = new window.Blob([response], {type: 'image/jpeg'})
          const qrUrl = window.URL.createObjectURL(response)
          this.proc_img = qrUrl;
          }
        );
      }

    }
  }
</script>

<style scoped>

</style>
