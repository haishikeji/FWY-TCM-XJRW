<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工单类型" prop="sceneTypeId">
        <el-row >
          <el-col :span="12">
            <el-select v-model="queryParams.dictType" filterable placeholder="请选择" @change="handleWorkTypeChange_1">
              <el-option
                v-for="item in typeOptions_1"
                :key="item.dictId"
                :label="item.dictName"
                :value="item.dictType"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="12">
            <el-select v-model="queryParams.sceneTypeId" filterable placeholder="请选择">
              <el-option
                v-for="item in sceneOptions_1"
                :key="item.dictCode"
                :label="item.dictLabel"
                :value="item.dictCode"
              />
            </el-select>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="描述" prop="describe">
        <el-input
          v-model="queryParams.describe"
          placeholder="请输入描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['work:order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['work:order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['work:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['work:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工单ID" align="center" prop="orderId" />
      <el-table-column label="工单类型" align="center" prop="sceneTypeId" />
      <el-table-column label="地址" align="center" prop="address" />
      <el-table-column label="描述" align="center" prop="describe" />
      <el-table-column label="图片" align="center" prop="images" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['work:order:edit']"
          ></el-button>
          <el-button
            size="mini"
            type="danger"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['work:order:remove']"
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
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工单类型" prop="sceneTypeId">
<!--          <el-input v-model="form.sceneTypeId" placeholder="请输入工单类型" />-->
          <el-row >
            <el-col :span="12">
              <el-select v-model="form.dictType" filterable placeholder="请选择" @change="handleWorkTypeChange">
                <el-option
                  v-for="item in typeOptions"
                  :key="item.dictId"
                  :label="item.dictName"
                  :value="item.dictType"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="12">
              <el-select v-model="form.sceneTypeId" filterable placeholder="请选择">
                <el-option
                  v-for="item in sceneOptions"
                  :key="item.dictCode.toString()"
                  :label="item.dictLabel"
                  :value="item.dictCode.toString()"
                />
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="描述" prop="describe">
<!--          <el-input v-model="form.describe" placeholder="请输入描述" />-->
          <el-input v-model="form.describe" :rows="5" type="textarea" placeholder="请输入描述"></el-input>
        </el-form-item>
<!--        <el-form-item label="图片" prop="images">-->
<!--          <el-input v-model="form.images" placeholder="请输入图片" />-->
<!--        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { listOrder, getOrder, delOrder, addOrder, updateOrder } from "@/api/work/functional/workorder";
  import { getDicts } from "@/api/work/dict/scene";
  import { optionselect as getDictOptionselect } from "@/api/work/dict/type";

  export default {
    name: "Order",
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
        orderList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          sceneTypeId: null,
          address: null,
          describe: null,
          images: null,
          userId: null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          sceneTypeId:[
            { required: true, message: "请选择工单类型", trigger: "blur" },
          ],
          address:[
            { required: true, message: "请输入工单地址", trigger: "blur" },
          ],
          describe:[
            { required: true, message: "请输入工单描述", trigger: "blur" },
          ],
        },
        // 类型数据字典
        typeOptions: [],
        // 类型数据字典
        sceneOptions: [],
        // 类型数据字典
        typeOptions_1: [],
        // 类型数据字典
        sceneOptions_1: [],
      };
    },
    created() {
      this.getList();
      this.getTypeList_1();
    },
    methods: {
      /** 查询【请填写功能名称】列表 */
      getList() {
        this.loading = true;
        listOrder(this.queryParams).then(response => {
          this.orderList = response.rows;
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
          orderId: null,
          sceneTypeId: null,
          address: null,
          describe: null,
          images: null,
          delFlag: null,
          userId: null,
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
        this.ids = selection.map(item => item.orderId)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加工单";
        this.getTypeList();
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const orderId = row.orderId || this.ids
        getOrder(orderId).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改【请填写功能名称】";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.orderId != null) {
              updateOrder(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addOrder(this.form).then(response => {
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
        const orderIds = row.orderId || this.ids;
        this.$modal.confirm('是否确认删除工单编号为"' + orderIds + '"的数据项？').then(function() {
          return delOrder(orderIds);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('system/order/export', {
          ...this.queryParams
        }, `order_${new Date().getTime()}.xlsx`)
      },
      /** 查询字典类型列表 */
      getTypeList() {
        getDictOptionselect().then(response => {
          this.typeOptions = response.data;
        });
      },
      handleWorkTypeChange(e){
        getDicts(e).then(response => {
          this.form.sceneTypeId = null;
          this.sceneOptions = response.data;
        });
      },
      /** 查询字典类型列表 */
      getTypeList_1() {
        getDictOptionselect().then(response => {
          this.typeOptions_1 = response.data;
        });
      },
      handleWorkTypeChange_1(e){
        getDicts(e).then(response => {
          this.queryParams.sceneTypeId = null;
          this.sceneOptions_1 = response.data;
          if (response.data.length > 0){
            this.queryParams.sceneTypeId = response.data[0].dictCode
          }
        });
      },
    }
  };
</script>
