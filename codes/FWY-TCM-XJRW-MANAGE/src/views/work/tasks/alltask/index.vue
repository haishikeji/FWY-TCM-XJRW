<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流程名称" prop="processName">
        <el-input
          v-model="queryParams.processName"
          placeholder="请输入流程名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
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
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="primary"-->
      <!--          plain-->
      <!--          icon="el-icon-plus"-->
      <!--          size="mini"-->
      <!--          @click="handleAdd"-->
      <!--          v-hasPermi="['work:type:add']"-->
      <!--        >部署</el-button>-->
      <!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务名称" align="center" prop="taskName" />
      <el-table-column label="流程实例编号" align="center" prop="processInstanceId" :show-overflow-tooltip="true" />
      <el-table-column label="执行实例编号" align="center" prop="executionId" :show-overflow-tooltip="true" />
      <el-table-column label="业务号" align="center" prop="businessKey" :show-overflow-tooltip="true" />
      <el-table-column label="流程名称" align="center" prop="processName" :show-overflow-tooltip="true" />
      <el-table-column label="发起人" align="center" prop="starter" :show-overflow-tooltip="true" />
      <el-table-column label="办理人" align="center" prop="assignee" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="流程启动时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.handle == 1"
            type="danger"
            size="mini"
            icon="el-icon-s-promotion"
            @click="handleOrder(scope.row)"
            v-hasPermi="['work:task:complete']"
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
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="模型标识" prop="key">
          <el-input v-model="form.key" placeholder="请输入模型标识" />
        </el-form-item>
        <el-form-item label="模型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { listAllTask } from "@/api/work/tasks/task";

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
          processName: undefined,
          taskName: undefined,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          key: [
            {required: true, message: "模型标识不能为空", trigger: "blur"}
          ],
          name: [
            {required: true, message: "模型名称不能为空", trigger: "blur"}
          ]
        },
        curUser:{}
      }
    },
    mounted() {
      this.curUser = this.$store.state.user
    },
    created() {
      this.getList();
    },
    methods: {
      /** 查询列表 */
      getList() {
        this.loading = true;
        listAllTask(this.queryParams).then(response => {
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
      // 表单重置
      reset() {
        this.form = {
          key: undefined,
          name: undefined,
          description: undefined
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
        this.dateRange = [];
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加模型";
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length != 1
        this.multiple = !selection.length
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        console.log(row);
        this.reset();
        const modelId = row.id || this.ids
        getModel(modelId).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改模型";
        });
      },
      /** 提交按钮 */
      submitForm: function () {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != undefined) {
              updateModel(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addModel(this.form).then(response => {
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
        const modelIds = row.id || this.ids;
        this.$modal.confirm('是否确认删除模型编号为"' + modelIds + '"的数据项？').then(function () {
          return delModel(modelIds);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 发布 */
      handleDeploy(row) {
        deploymentModel(row.id).then(response => {
          this.getList();
          this.$modal.msgSuccess("发布成功");
        });
      },
      /** 设计 */
      handleDesign(row) {
        this.$router.push("/work/manager-model/index/" + row.id);
      },
      handleDefinition() {

      },
      handleFlowChart() {

      },
      handleOrder(row){
        //这里需要根据formKey不用跳转到不同的页面进行处理
        var key = row.formKey;
        switch (key) {
          case "check":
            this.$router.push("/work/check-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "dispatch":
            this.$router.push("/work/dispatch-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "receive":
            this.$router.push("/work/receive-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "transfer":
            this.$router.push("/work/transfer-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "accept":
            this.$router.push("/work/accept-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "handle":
            this.$router.push("/work/handle-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "complete":
            this.$router.push("/work/complete-order/index/" + row.taskId+"/"+row.businessKey);
            break;
          case "userevaluate":
            this.$router.push("/work/evaluate-order/index/" + row.taskId+"/"+row.businessKey);
            break;
        }
      }
    }
  }
</script>

<style scoped>

</style>
