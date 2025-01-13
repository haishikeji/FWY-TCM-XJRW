<template>
<div class="app-container" id="container">
    <!-- <div class="ok-click" @click="mapOk">确认</div> -->
</div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader';
// import AMap from 'AMap';

export default {
    name: "Lnglat",
    props: {
        lnglat: {
            type: Array,
            default: null
        }
    },
    data() {
        return {
            map: null, //初始化 map 对象
            marker: null,
            // lnglat: null
        };
    },
    watch: {
        lnglat(val) {
            // debugger
            console.log(val);
            if(this.map){
              this.map.destroy()
              this.initMap()
            }
        }
    },
    created() {
        // DOM初始化完成进行地图初始化
        this.initMap();
    },
    mounted() {
        //挂载阶段调用，DOM初始化完成进行地图初始化
        // this.initMap();
    },
    methods: {
        initMap() {
            const _this = this;
            AMapLoader.load({
                key: "ba8ab1308886fb552aefb321333b70fe", //此处填入我们注册账号后获取的Key
                version: "2.0", //指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
                plugins: ['AMap.Geocoder', 'AMap.Geolocation'], //需要使用的的插件列表，如比例尺'AMap.Scale'等
            }).then((AMap) => {
                _this.map = new AMap.Map("container", { //设置地图容器id
                    viewMode: "3D", //是否为3D地图模式
                    zoom: 13, //初始化地图级别
                    center: this.lnglat, //初始化地图中心点位置
                });
                _this.addMarker(_this.lnglat)
                // let lnglat = null;
                // AMap.plugin('AMap.Geolocation', function () {
                //     var geolocation = new AMap.Geolocation({
                //         // 是否使用高精度定位，默认：true
                //         enableHighAccuracy: true,
                //         // 设置定位超时时间，默认：无穷大
                //         timeout: 1000,
                //         // 定位按钮的停靠位置的偏移量，默认：Pixel(10, 20)
                //         buttonOffset: new AMap.Pixel(10, 20),
                //         //  定位成功后调整地图视野范围使定位位置及精度范围视野内可见，默认：false
                //         zoomToAccuracy: true,
                //         //  定位按钮的排放位置,  RB表示右下
                //         // buttonPosition: 'RB'
                //     })

                //     geolocation.getCurrentPosition((status, result) => {
                //         console.log(status);
                //         console.log(result);
                //         _this.lnglat = result.position
                //         if (status == 'complete' && result.info == "SUCCESS") {
                //             _this.map = new AMap.Map("container", { //设置地图容器id
                //                 viewMode: "3D", //是否为3D地图模式
                //                 zoom: 13, //初始化地图级别
                //                 center: [_this.lnglat.lng, _this.lnglat.lat], //初始化地图中心点位置
                //             });
                //             _this.addMarker(_this.lnglat)
                //         } else {
                //             _this.map = new AMap.Map("container", { //设置地图容器id
                //                 viewMode: "3D", //是否为3D地图模式
                //                 zoom: 13, //初始化地图级别
                //                 center: [112.982279, 28.19409], //初始化地图中心点位置
                //             });
                //         }
                //         // _this.map.on('click', function(ev) {
                //         //   console.log(ev.lnglat.getLng(), ev.lnglat.getLat());
                //         //   _this.addMarker(ev.lnglat)
                //         //   _this.lnglat = ev.lnglat
                //         // });
                //     })

                // })

            }).catch(e => {
                console.log(e);
            })
        },
        /**
         * 添加标记
         */
        addMarker(position) {
            if (this.marker) {
                this.map.remove(this.marker)
            }
            this.marker = new AMap.Marker({
                position: position,
                // title: '北京'
            });
            // marker.add(this.map);
            this.map.add(this.marker)
        },
        /**
         * 获取坐标位置信息
         */
        regeoCode(position, classAmap) {
            let geocoder = new classAmap.Geocoder({

            });
            geocoder.getAddress(position, (status, result) => {
                console.log(status);
                console.log(result);
            })
        },
        mapOk() {
            if (!this.lnglat) {
                this.$message.error('请选择坐标')
                return
            }
            this.$emit('mapOk', this.lnglat)
        }
    }
};
</script>

<style>
#container {
    height: 200px;
}

.app-container {
    position: relative;
}

.ok-click {
    position: absolute;
    bottom: 20px;
    right: 20px;
    z-index: 999999;
    padding: 10px 20px;
    background-color: #409EFF;
    color: #fff;
    border-radius: 4px;
    cursor: pointer;
}
</style>
