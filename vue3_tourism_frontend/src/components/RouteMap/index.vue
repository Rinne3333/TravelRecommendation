<template>
  <div class="route-map-container">
    <div class="map-controls">
      <div class="control-group">
        <el-button-group>
          <el-button
            :type="mapType === 'satellite' ? 'primary' : 'default'"
            @click="changeMapType('satellite')"
            size="small"
          >
            卫星图
          </el-button>
          <el-button
            :type="mapType === 'roadmap' ? 'primary' : 'default'"
            @click="changeMapType('roadmap')"
            size="small"
          >
            地图
          </el-button>
        </el-button-group>
      </div>
      <div class="control-group">
        <el-button @click="fitView" size="small">
          <el-icon><FullScreen /></el-icon>
          适应视野
        </el-button>
        <el-button @click="toggleTraffic" size="small">
          <el-icon><Location /></el-icon>
          {{ showTraffic ? "隐藏路况" : "显示路况" }}
        </el-button>
      </div>
    </div>

    <div :id="mapId" class="map-container"></div>

    <div class="map-legend">
      <div class="legend-item">
        <div class="legend-icon start"></div>
        <span>起点</span>
      </div>
      <div class="legend-item">
        <div class="legend-icon waypoint"></div>
        <span>途经景点</span>
      </div>
      <div class="legend-item">
        <div class="legend-icon end"></div>
        <span>终点</span>
      </div>
      <div class="legend-item">
        <div class="legend-line"></div>
        <span>推荐路线</span>
      </div>
    </div>

    <div v-if="loading" class="map-loading">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>地图加载中...</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { FullScreen, Location, Loading } from "@element-plus/icons-vue";
import AMapLoader from "@amap/amap-jsapi-loader";
import { MAP_CONFIG, SPOT_COORDINATES } from "@/utils/mapConfig";

export default defineComponent({
  name: "RouteMap",
  components: {
    FullScreen,
    Location,
    Loading
  },
  props: {
    spotNames: {
      type: Array as () => string[],
      default: () => []
    },
    routeTitle: {
      type: String,
      default: "旅游路线"
    }
  },
  setup(props) {
    // 响应式数据
    const loading = ref(true);
    const mapType = ref("roadmap");
    const showTraffic = ref(false);
    const mapId = ref(`route-map-${Date.now()}`);

    // 地图相关变量
    let map: any = null;
    let markers: any[] = [];
    let polyline: any = null;
    let trafficLayer: any = null;

    // 景点坐标数据
    const spotCoordinates = ref(SPOT_COORDINATES);

    // 初始化地图
    const initMap = async () => {
      try {
        loading.value = true;
        console.log("开始初始化地图，容器ID:", mapId.value);
        console.log("景点列表:", props.spotNames);

        const AMap = await AMapLoader.load({
          key: MAP_CONFIG.AMAP_API_KEY,
          version: MAP_CONFIG.AMAP_VERSION,
          plugins: MAP_CONFIG.AMAP_PLUGINS
        });

        console.log("高德地图API加载成功");

        // 等待DOM更新
        await new Promise((resolve) => setTimeout(resolve, 100));

        // 创建地图实例
        map = new AMap.Map(mapId.value, MAP_CONFIG.DEFAULT_MAP_OPTIONS);
        console.log("地图实例创建成功");

        // 监听地图加载完成事件
        map.on("complete", () => {
          console.log("地图加载完成");
          loading.value = false;
          // 绘制路线
          drawRoute();
        });

        // 添加地图控件
        map.addControl(new AMap.Scale());
        map.addControl(
          new AMap.ToolBar({
            position: {
              top: "10px",
              right: "10px"
            }
          })
        );

        // 创建路况图层
        trafficLayer = new AMap.TileLayer.Traffic({
          zIndex: 10,
          autoRefresh: true,
          interval: 180
        });
      } catch (error) {
        console.error("地图初始化失败:", error);
        ElMessage.error("地图加载失败，请检查网络连接");
        loading.value = false;
      }
    };

    // 绘制路线
    const drawRoute = () => {
      console.log("开始绘制路线");
      console.log("地图实例:", map);
      console.log("景点名称列表:", props.spotNames);

      if (!map) {
        console.warn("地图实例不存在");
        return;
      }

      if (!props.spotNames.length) {
        console.warn("景点名称列表为空");
        return;
      }

      // 清除之前的标记和路线
      clearMap();

      // 获取景点坐标
      const coordinates: [number, number][] = [];
      const validSpots: string[] = [];

      props.spotNames.forEach((spotName) => {
        const coord = spotCoordinates.value[spotName];
        console.log(`景点 ${spotName} 的坐标:`, coord);
        if (coord) {
          coordinates.push(coord);
          validSpots.push(spotName);
        } else {
          console.warn(`未找到景点 ${spotName} 的坐标`);
        }
      });

      console.log("有效坐标:", coordinates);
      console.log("有效景点:", validSpots);

      if (coordinates.length === 0) {
        ElMessage.warning("未找到有效的景点坐标信息");
        return;
      }

      // 创建标记点
      coordinates.forEach((coord, index) => {
        const isStart = index === 0;
        const isEnd = index === coordinates.length - 1;

        console.log(`创建标记点 ${index + 1}: ${validSpots[index]}`, coord);

        // 使用简单的圆形标记，避免SVG问题
        const marker = new (window as any).AMap.Marker({
          position: coord,
          title: validSpots[index],
          content: `<div style="
            width: 24px; 
            height: 24px; 
            border-radius: 50%; 
            background: ${isStart ? "#52c41a" : isEnd ? "#f5222d" : "#1890ff"}; 
            border: 2px solid white; 
            box-shadow: 0 2px 6px rgba(0,0,0,0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 12px;
            font-weight: bold;
          ">${isStart ? "起" : isEnd ? "终" : index + 1}</div>`
        });

        // 添加信息窗口
        const infoWindow = new (window as any).AMap.InfoWindow({
          content: `
            <div class="marker-info">
              <h4>${validSpots[index]}</h4>
              <p>${isStart ? "起点" : isEnd ? "终点" : `第${index + 1}站`}</p>
              <p>经纬度: ${coord[1].toFixed(6)}, ${coord[0].toFixed(6)}</p>
            </div>
          `,
          offset: new (window as any).AMap.Pixel(0, -30)
        });

        marker.on("click", () => {
          infoWindow.open(map, coord);
        });

        map.add(marker);
        markers.push(marker);
        console.log(`标记点 ${index + 1} 添加成功`);
      });

      console.log(`总共添加了 ${markers.length} 个标记点`);

      // 绘制路线
      if (coordinates.length > 1) {
        console.log("开始绘制驾车路线");
        drawDrivingRoute(coordinates);
      }

      // 调整视野以包含所有点
      if (coordinates.length > 0) {
        console.log("调整地图视野");
        setTimeout(() => {
          map.setFitView(markers, false, [50, 50, 50, 50]);
        }, 500);
      }
    };

    // 绘制驾车路线
    const drawDrivingRoute = (coordinates: [number, number][]) => {
      console.log("创建驾车路线规划实例");

      const driving = new (window as any).AMap.Driving({
        map: map,
        panel: null,
        policy: (window as any).AMap.DrivingPolicy.LEAST_TIME,
        ferry: 1,
        polyOptions: MAP_CONFIG.ROUTE_STYLE
      });

      // 监听路线规划成功事件
      driving.on("complete", (result: any) => {
        console.log("路线规划成功:", result);
      });

      // 监听路线规划失败事件
      driving.on("error", (error: any) => {
        console.error("路线规划失败:", error);
        ElMessage.warning("路线规划失败，可能是景点间距离过远或无法通行");
      });

      // 如果只有两个点，直接搜索路线
      if (coordinates.length === 2) {
        console.log(
          "两点路线规划:",
          coordinates[0],
          "->",
          coordinates[coordinates.length - 1]
        );
        driving.search(coordinates[0], coordinates[coordinates.length - 1]);
      } else {
        // 多个点的情况，设置途经点
        const waypoints = coordinates.slice(1, -1);
        console.log("多点路线规划:");
        console.log("起点:", coordinates[0]);
        console.log("途经点:", waypoints);
        console.log("终点:", coordinates[coordinates.length - 1]);

        driving.search(coordinates[0], coordinates[coordinates.length - 1], {
          waypoints: waypoints
        });
      }
    };

    // 清除地图
    const clearMap = () => {
      if (markers.length > 0) {
        map.remove(markers);
        markers = [];
      }
      if (polyline) {
        map.remove(polyline);
        polyline = null;
      }
    };

    // 切换地图类型
    const changeMapType = (type: string) => {
      if (!map) return;

      mapType.value = type;
      if (type === "satellite") {
        map.setMapStyle("amap://styles/satellite");
      } else {
        map.setMapStyle("amap://styles/normal");
      }
    };

    // 适应视野
    const fitView = () => {
      if (map && markers.length > 0) {
        map.setFitView(markers, false, [50, 50, 50, 50]);
      }
    };

    // 切换路况显示
    const toggleTraffic = () => {
      if (!map || !trafficLayer) return;

      showTraffic.value = !showTraffic.value;
      if (showTraffic.value) {
        map.add(trafficLayer);
      } else {
        map.remove(trafficLayer);
      }
    };

    // 监听景点变化
    watch(
      () => props.spotNames,
      (newSpotNames) => {
        console.log("景点列表发生变化:", newSpotNames);
        if (map && newSpotNames.length > 0) {
          setTimeout(() => {
            drawRoute();
          }, 100);
        }
      },
      { deep: true, immediate: false }
    );

    // 生命周期
    onMounted(() => {
      console.log("组件已挂载，开始初始化地图");
      // 延迟一下确保DOM完全渲染
      setTimeout(() => {
        initMap();
      }, 200);
    });

    onUnmounted(() => {
      if (map) {
        map.destroy();
      }
    });

    return {
      loading,
      mapType,
      showTraffic,
      mapId,
      changeMapType,
      fitView,
      toggleTraffic
    };
  }
});
</script>

<style scoped lang="scss">
.route-map-container {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f5f5;
}

.map-controls {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 1000;
  display: flex;
  gap: 12px;

  .control-group {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    padding: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    backdrop-filter: blur(10px);
  }
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-legend {
  position: absolute;
  bottom: 16px;
  right: 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);

  .legend-item {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;
    font-size: 12px;

    &:last-child {
      margin-bottom: 0;
    }

    .legend-icon {
      width: 16px;
      height: 16px;
      border-radius: 50%;

      &.start {
        background: #52c41a;
      }

      &.waypoint {
        background: #1890ff;
      }

      &.end {
        background: #f5222d;
      }
    }

    .legend-line {
      width: 16px;
      height: 3px;
      background: #3366ff;
      border-radius: 2px;
    }
  }
}

.map-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #666;

  .el-icon {
    font-size: 32px;
    margin-bottom: 8px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

// 信息窗口样式
:global(.marker-info) {
  padding: 8px;

  h4 {
    margin: 0 0 8px 0;
    color: #333;
    font-size: 14px;
  }

  p {
    margin: 4px 0;
    color: #666;
    font-size: 12px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .route-map-container {
    height: 300px;
  }

  .map-controls {
    flex-direction: column;
    gap: 8px;
  }

  .map-legend {
    bottom: 8px;
    right: 8px;
    padding: 8px;
    font-size: 11px;
  }
}
</style>
