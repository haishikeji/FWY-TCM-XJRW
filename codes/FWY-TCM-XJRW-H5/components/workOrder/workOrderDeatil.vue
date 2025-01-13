<template>
	<view class="container">
			<u-navbar v-if="!is_weixin" title="工单处理" :autoBack="true" safeAreaInsetTop placeholder>
			</u-navbar>
		<view class="content">
			<u-form :model="form" :rules="rules" ref="uForm">
				<u-form-item label="类型" prop="type" borderBottom :customStyle="customStyle">
					<u-input v-model="form.type" disabledColor="#ffffff" placeholder="请选择类型" border="none" disabled>
					</u-input>
					<u-icon slot="right" name="arrow-right"></u-icon>
				</u-form-item>
				<u-form-item label="地址" prop="address" :customStyle="customStyle">
					<u-input v-model="form.address" disabled />
				</u-form-item>
				<u-form-item label="描述" prop="describe" :customStyle="customStyle">
					<!-- <u-input v-model="form.describe" /> -->
					<u-textarea v-model="form.describe" count placeholder="请输入内容" :confirmType="confirmType" disabled>
					</u-textarea>
				</u-form-item>
				<u-form-item v-if="showChose" label="接收" prop="receiveorder" :customStyle="customStyle">
					<u-radio-group v-model="form.receiveorder" placement="row">
						<u-radio :customStyle="{marginRight: '16px'}" v-for="(item, index) in radiolist" :key="index"
							:label="item.name" :name="item.name">
						</u-radio>
					</u-radio-group>
				</u-form-item>
				<u-form-item v-if="showStar" label="评价" prop="rate" :customStyle="customStyle">
					<u-rate v-model="form.rate" size="30" count="5" :allowHalf="true" :touchable="true"></u-rate>
				</u-form-item>
				<u-form-item label="备注" prop="comment" :customStyle="customStyle">
					<!-- <u-input v-model="form.describe" /> -->
					<u-textarea v-model="form.comment" count placeholder="请输入内容" :confirmType="confirmType">
					</u-textarea>
				</u-form-item>
			</u-form>
			<u-picker :show="show" ref="uPicker" closeOnClickOverlay :columns="columns" keyName="lable"
				@confirm="confirm" @change="changeHandler" @cancel="cancel" @close="close">
			</u-picker>
		</view>
		<u-button style="width:93vw;margin:20px auto;" type="primary" @click="submit">确认提交</u-button>
	</view>
</template>

<script>
	import Api from '../../common/api.js';
	const userInfo = uni.getStorageSync('userInfo')

	export default {
		data() {
			return {
				typeOptions: [], // 工单类型
				sceneOptions: [], // 场景类型
				customStyle: {},
				confirmType:'done',
				show: false,
				form: {
					dictType: '',
					sceneTypeId: '',
					address: '',
					describe: '',
					receiveorder: '',
					comment: '',
					type: '',
					rate: ''
				},
				rules: {
					receiveorder: {
						type: 'string',
						min: 1,
						required: true,
						message: '请选择是否接收工单',
						trigger: ['blur', 'change']
					}
				},
				//横向排列形式数据
				radiolist: [{
						name: '接受',
						disabled: false
					},
					{
						name: '拒绝',
						disabled: false
					}
				],
				columns: [],
				orderId: '',
				taskId: '',
				typeName: '',
				showChose: false,
				showStar: false,
				formKey: ''
			}
		},
		onLoad(options) {
			console.log(options);
			this.orderId = options.orderId;
			this.taskId = options.taskId;
			this.formKey = options.formKey;
			if (options.formKey == 'receive') {
				this.showChose = true
			}
			if (options.formKey == 'userevaluate') {
				this.showStar = true
			}
			this.getOrderInfo();
		},
		computed: {
			is_weixin() {
				let ua = navigator.userAgent.toLowerCase();
				return ua.indexOf('micromessenger') != -1;
			},
		},
		methods: {
			getOrderInfo() {
				Api.getOrderInfo(this.orderId).then(res => {
					console.log(res)
					if (res.code == 200) {
						this.form.dictType = res.data.dictType
						this.form.sceneTypeId = res.data.sceneTypeId
						this.form.address = res.data.address
						this.form.describe = res.data.describe
						this.getWorkTypeList()
					}
				})
			},
			getWorkTypeList() {
				Api.getWorkTypeList().then(res => {
					console.log(res);
					if (res.code == 200) {
						var dictType = this.form.dictType
						var sceneTypeId = this.form.sceneTypeId
						for (let i = 0; i < res.data.length; i++) {
							var it = res.data[i]
							console.log(it.dictType, dictType)
							console.log(it.dictType == dictType)
							if (it.dictType == dictType) {
								this.typeName = it.dictName
							}

							var item = {
								id: it.dictId,
								lable: it.dictName,
								value: it.dictType
							}
							this.typeOptions.push(item)
						}
						this.columns = [];
						this.columns.push(this.typeOptions);
						var param = {
							type: dictType
						};
						this.getSceneTypeList(param, sceneTypeId)
					}
				})
			},
			getSceneTypeList(param, sceneTypeId) {
				Api.getSceneTypeList(param).then(res => {
					console.log(res);
					if (res.code == 200) {
						this.sceneOptions = [];
						for (let i = 0; i < res.data.length; i++) {
							var it = res.data[i]
							if (it.dictCode == sceneTypeId) {
								this.typeName = this.typeName + " - " + it.dictLabel
							}
							var item = {
								id: it.dictCode,
								lable: it.dictLabel,
								value: it.dictCode
							}
							this.sceneOptions.push(item)
						}
						console.log(this.typeName)
						this.form.type = this.typeName
						// this.$refs.uPicker.setColumnValues(1, this.sceneOptions)
					}
				})
			},
			submit() {
				// uni.navigateBack()
				this.$refs.uForm.validate().then(res => {
					//根据类型进行不同处理
					var data = {}
					if (this.formKey == 'receive') {
						var value = this.form.receiveorder == '接受' ? 'true' : 'false'
						data = {
							receiveorder: value,
							comment: this.form.comment,
						}
					} else if (this.formKey == 'userevaluate') {
						var content = "";
						var rate = this.form.rate
						if (rate) {
							content = "评分：" + rate;
						}
						if (this.form.comment) {
							content += "；留言：" + this.form.comment;
						}

						data = {
							comment: content,
							user: userInfo.userId
						}
					} else {
						data = {
							comment: this.form.comment,
							user: userInfo.userId
						}
					}

					console.log(data)
					// return;
					Api.completeTask(JSON.stringify(data), this.taskId)
						.then(res => {
							console.log(res.code)
							console.log(res.code == 200)
							if (res.code == 200) {
								uni.showToast({
									title: '提交成功',
									icon: 'none'
								})
								uni.navigateBack()
							} else {
								uni.showToast({
									title: res.msg,
									icon: 'none'
								})
							}
						})
				}).catch(errors => {
					console.log(errors)
					uni.$u.toast('校验失败')
				})
			},
			hideKeyboard() {

			},
			changeHandler(e) {
				const {
					columnIndex,
					value,
					values, // values为当前变化列的数组内容
					index,
					// 微信小程序无法将picker实例传出来，只能通过ref操作
					picker = this.$refs.uPicker
				} = e
				var param = {
					type: value[0].value
				};
				Api.getSceneTypeList(param).then(res => {
					console.log(res);
					if (res.code == 200) {
						this.sceneOptions = [];
						for (let i = 0; i < res.data.length; i++) {
							var it = res.data[i]
							var item = {
								id: it.dictCode,
								lable: it.dictLabel,
								value: it.dictCode
							}
							this.sceneOptions.push(item)
						}
						picker.setColumnValues(1, this.sceneOptions)
					}
				})
			},
			// 回调参数为包含columnIndex、value、values
			confirm(e) {
				console.log('confirm', e)
				this.show = false
				var type = e.value[0].lable + " - " + e.value[1].lable
				this.form.type = type
				this.form.sceneTypeId = e.value[1].value
				this.$refs.uForm.validateField('form.sceneTypeId')
			},
			navigateBack() {
				uni.navigateBack()
			},
			close() {
				// console.log('close');
				this.show = false
			},
			cancel() {
				// console.log('cancel');
				this.show = false
			}
		},
	}
</script>

<style>
	.container {
		width: 100%;
	}

	.content {
		width: 92%;
		margin: auto;
	}
</style>
