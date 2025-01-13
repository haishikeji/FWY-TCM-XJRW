<template>
  <div class="app-container" v-loading="loading">
    <div class="check-box">
      <div class="left">
        <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="工单类型" prop="sceneTypeId">
            <el-row >
              <el-col :span="6">
                <el-select v-model="form.dictType" filterable disabled >
                  <el-option
                    v-for="item in typeOptions"
                    :key="item.dictId"
                    :label="item.dictName"
                    :value="item.dictType"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="6">
                <el-select v-model="form.sceneTypeId" filterable disabled>
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
            <el-input v-model="form.address" placeholder="请输入地址" disabled/>
          </el-form-item>
          <el-form-item label="描述" prop="describe">
            <el-input v-model="form.describe" :rows="5" type="textarea" placeholder="请输入描述" disabled></el-input>
          </el-form-item>
          <!--          <el-form-item label="图片" prop="images">-->
          <!--            <el-input v-model="form.images" placeholder="请输入图片" />-->
          <!--          </el-form-item>-->

          <!--接收-->
          <el-form-item label="接收工单" prop="describe">
            <el-radio-group v-model="form.receiveorder">
              <el-radio label="true">接受</el-radio>
              <el-radio label="false">拒绝</el-radio>
            </el-radio-group>
            <!--            <el-radio v-model="form.receiveorder" label="true">接受</el-radio>-->
            <!--            <el-radio v-model="form.receiveorder" label="false">拒绝</el-radio>-->
          </el-form-item>
          <el-form-item label="备注" prop="comment">
            <el-input v-model="form.comment" :rows="5" type="textarea" placeholder="请输入"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer" style="padding-left: 80px;">
          <el-button type="primary" @click="submitHandler">提 交</el-button>
        </div>
      </div>

      <div class="right">
        <div id="vertical-timeline">
          <el-timeline>
            <el-timeline-item  v-for="(item, index) in historyList" :key="index" :timestamp="item.startTime" placement="top"
                               icon="el-icon-collection-tag"
                               :color="item.endTime?'#0bbd87':''"
                               size="large">
              <el-card :body-style="{ padding: '8px' }">
                <h3>{{item.assignee}}<b>{{item.taskName}}</b></h3>
                <p>{{(item.comment||'-')}}</p>
                <p> 结束 {{(item.endTime||'-')}}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>

<!--          <el-timeline>-->
<!--            <el-timeline-item-->
<!--              v-for="(item, index) in historyList"-->
<!--              :key="index"-->
<!--              icon="el-icon-collection-tag"-->
<!--              :color="item.endTime?'#0bbd87':''"-->
<!--              size="large"-->
<!--              :timestamp="item.startTime">-->
<!--              {{item.assignee + item.taskName}}-->
<!--&lt;!&ndash;              <div>&ndash;&gt;-->
<!--&lt;!&ndash;                <h3>{{item.assignee}}<b>{{item.taskName}}</b></h3>&ndash;&gt;-->
<!--&lt;!&ndash;                <p>{{(item.comment||'-')}}</p>&ndash;&gt;-->
<!--&lt;!&ndash;                <p> 结束 {{(item.endTime||'-')}}</p>&ndash;&gt;-->
<!--&lt;!&ndash;              </div>&ndash;&gt;-->
<!--            </el-timeline-item>-->
<!--          </el-timeline>-->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { getOrder } from "@/api/work/functional/workorder";
  import { getTaskHistory, completeTask } from "@/api/work/tasks/task";
  import { getDicts } from "@/api/work/dict/scene";
  import { optionselect as getDictOptionselect } from "@/api/work/dict/type";
  import router from '@/router';
  import store from '@/store'


  export default {
    name: "receiveOrder",
    data() {
      return {
        // 表单参数
        form: {
          receiveorder:undefined
        },
        // 表单校验
        rules: {},
        taskId:'',
        historyList:[],
        // 遮罩层
        loading: true,
        // 类型数据字典
        typeOptions: [],
        // 类型数据字典
        sceneOptions: [],
      }
    },
    created() {
      var params = this.$route.params;
      console.log(params)

      this.getOrderInfo(params.orderId);
      this.getTaskHistory(params.taskId);

      this.taskId = params.taskId
    },
    methods: {
      /**获取工单信息*/
      getOrderInfo(orderId){
        getOrder(orderId).then(response => {
          this.form = response.data;
          // this.form.receiveorder = 'true';

          this.getTypeList();
        });
      },
      /**获取流程记录*/
      getTaskHistory(taskId){
        getTaskHistory(taskId).then(data => {
          console.log(data);
          this.historyList = data;
        });
      },
      submitHandler(){
        console.log('提交')
        var data = {
          receiveorder:this.form.receiveorder,
          comment:this.form.comment
        }
        console.log(data)
        completeTask(data,this.taskId).then(response => {
          this.$modal.msgSuccess("提交成功");
          store.dispatch("tagsView/delView", router.currentRoute);
          this.$router.go(-1)
        });
      },
      /** 查询字典类型列表 */
      getTypeList() {
        getDictOptionselect().then(response => {
          this.typeOptions = response.data;
        });
        getDicts(this.form.dictType).then(response => {
          this.sceneOptions = response.data;
        });
        this.loading = false;
      },
    }
  }
</script>

<style scoped>
  .check-box{
    display: flex;
    flex-direction: row;
  }
  .left{
    flex: 2;
  }
  .right{
    flex: 1;
  }
</style>

