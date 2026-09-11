<template>
  <div class="route-detail-container">
    <!-- 顶部导航栏 -->
    <div class="top-navbar">
      <div class="navbar-content">
        <div class="nav-left">
          <el-button @click="$router.go(-1)" circle>
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <span class="nav-title">旅游路线详情</span>
        </div>
        <div class="nav-right">
          <el-button type="primary" @click="shareDialogVisible = true">
            <el-icon><Share /></el-icon>
            分享路线
          </el-button>
        </div>
      </div>
    </div>

    <!-- 路线头部信息 -->
    <div class="route-header">
      <div class="header-bg">
        <div class="header-overlay">
          <div class="header-content">
            <div class="route-avatar">
              <img :src="routeInfo.spotRouteAvatar" :alt="routeInfo.title" />
            </div>
            <div class="route-info">
              <h1 class="route-title">
                {{ routeInfo.title || "精品旅游路线" }}
              </h1>
              <p class="route-subtitle">{{ routeInfo.spotRouteDescription }}</p>
              <div class="route-meta">
                <div class="meta-item">
                  <el-icon><Clock /></el-icon>
                  <span>创建时间：{{ formatDate(routeInfo.createTime) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>管理员ID：{{ routeInfo.adminId }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><MapLocation /></el-icon>
                  <span>{{ routeInfo.spotNameList.length }}个景点</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="content-wrapper">
        <!-- 左侧内容 -->
        <div class="left-content">
          <!-- 路线概览 -->
          <el-card class="route-overview">
            <template #header>
              <h3>
                <el-icon><Guide /></el-icon>路线概览
              </h3>
            </template>
            <div class="overview-stats">
              <div class="stat-card">
                <div class="stat-number">
                  {{ routeInfo.spotNameList.length }}
                </div>
                <div class="stat-label">景点数量</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">{{ totalDistance.toFixed(1) }}</div>
                <div class="stat-label">总距离(km)</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">{{ estimatedTime }}</div>
                <div class="stat-label">预计时间</div>
              </div>
              <div class="stat-card">
                <div class="stat-number">
                  ¥{{ routeInfo.price || estimatedCost }}
                </div>
                <div class="stat-label">预估费用</div>
              </div>
            </div>
          </el-card>

          <!-- 景点列表 -->
          <el-card class="spots-list">
            <template #header>
              <h3>
                <el-icon><LocationFilled /></el-icon>景点详情
              </h3>
            </template>
            <div class="spots-timeline">
              <div
                v-for="(spot, index) in routeInfo.spotNameList"
                :key="index"
                class="timeline-item"
                :class="{
                  'last-item': index === routeInfo.spotNameList.length - 1
                }"
              >
                <div class="timeline-marker">
                  <div class="marker-number">{{ index + 1 }}</div>
                </div>
                <div class="timeline-content">
                  <div class="spot-card">
                    <div class="spot-image">
                      <img :src="getSpotImage(index)" :alt="spot" />
                      <div class="spot-badge">第{{ index + 1 }}站</div>
                    </div>
                    <div class="spot-info">
                      <h4 class="spot-name">{{ spot }}</h4>
                      <p class="spot-description">
                        {{ getSpotDescription(index) }}
                      </p>
                      <div class="spot-tags">
                        <el-tag size="small">5A景区</el-tag>
                        <el-tag size="small" type="success">推荐</el-tag>
                      </div>
                      <div class="spot-actions">
                        <el-button size="small" type="primary"
                          >查看详情</el-button
                        >
                        <el-button size="small">门票预订</el-button>
                      </div>
                    </div>
                  </div>
                  <!-- 距离信息 -->
                  <div
                    v-if="index < routeInfo.spotDistanceList.length"
                    class="distance-info"
                  >
                    <div class="distance-line">
                      <el-icon><Right /></el-icon>
                      <span
                        >{{
                          routeInfo.spotDistanceList[index].toFixed(1)
                        }}km</span
                      >
                      <span class="travel-time"
                        >约{{
                          Math.ceil(routeInfo.spotDistanceList[index] * 2)
                        }}分钟</span
                      >
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 路线地图 -->
          <el-card class="route-map">
            <template #header>
              <h3>
                <el-icon><Map /></el-icon>路线地图
              </h3>
            </template>
            <div class="map-content">
              <RouteMap
                :spotNames="routeInfo.spotNameList"
                :routeTitle="routeInfo.title"
              />
            </div>
          </el-card>
        </div>

        <!-- 右侧侧边栏 -->
        <div class="right-sidebar">
          <!-- 快速操作 -->
          <el-card class="quick-actions">
            <template #header>
              <h4>快速操作</h4>
            </template>
            <div class="action-buttons">
              <el-button type="primary" size="large" class="action-btn">
                <el-icon><Ticket /></el-icon>
                一键预订全程
              </el-button>
              <el-button type="success" size="large" class="action-btn">
                <el-icon><ShoppingCart /></el-icon>
                添加到购物车
              </el-button>
              <el-button type="warning" size="large" class="action-btn">
                <el-icon><Star /></el-icon>
                收藏路线
              </el-button>
            </div>
          </el-card>

          <!-- 路线信息 -->
          <el-card class="route-details">
            <template #header>
              <h4>路线信息</h4>
            </template>
            <div class="detail-item">
              <span class="detail-label">适合人群：</span>
              <span class="detail-value">家庭出游、情侣旅行</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">游玩时长：</span>
              <span class="detail-value">{{ routeInfo.duration }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">参与人数：</span>
              <span class="detail-value"
                >{{ routeInfo.participants }}人已体验</span
              >
            </div>
            <div class="detail-item">
              <span class="detail-label">难度等级：</span>
              <span class="detail-value">{{ routeInfo.difficulty }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">推荐指数：</span>
              <span class="detail-value">
                <el-rate :model-value="routeInfo.rating" disabled show-score />
              </span>
            </div>
          </el-card>

          <!-- 贴心提示 -->
          <el-card class="tips-card">
            <template #header>
              <h4>
                <el-icon><Warning /></el-icon>贴心提示
              </h4>
            </template>
            <ul class="tips-list">
              <li>建议提前预订门票，避免排队等候</li>
              <li>携带舒适的运动鞋，部分景点需要步行</li>
              <li>关注天气变化，做好防晒防雨准备</li>
              <li>保持手机电量充足，方便导航和联系</li>
              <li>尊重当地文化，文明旅游</li>
            </ul>
          </el-card>

          <!-- 相关推荐 -->
          <el-card class="recommendations">
            <template #header>
              <h4>相关推荐</h4>
            </template>
            <div class="recommend-list">
              <div
                v-for="item in relatedRoutes"
                :key="item.id"
                class="recommend-item"
              >
                <img
                  :src="item.image"
                  :alt="item.title"
                  class="recommend-image"
                />
                <div class="recommend-info">
                  <h5>{{ item.title }}</h5>
                  <p>{{ item.spots }}个景点</p>
                  <span class="recommend-price">¥{{ item.price }}起</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享旅游路线" width="600px">
      <div class="share-dialog-content">
        <div class="share-preview">
          <div class="preview-header">
            <img
              :src="routeInfo.spotRouteAvatar"
              alt=""
              class="preview-avatar"
            />
            <div class="preview-info">
              <h4>{{ routeInfo.title || "精品旅游路线" }}</h4>
              <p>
                {{ routeInfo.spotNameList.length }}个景点 ·
                {{ totalDistance.toFixed(1) }}km
              </p>
              <div class="preview-spots">
                <el-tag
                  v-for="(spot, index) in routeInfo.spotNameList.slice(0, 3)"
                  :key="index"
                  size="small"
                >
                  {{ spot }}
                </el-tag>
                <span v-if="routeInfo.spotNameList.length > 3">...</span>
              </div>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="share-options">
          <el-tabs v-model="shareActiveTab">
            <el-tab-pane label="链接分享" name="link">
              <div class="share-link-section">
                <div class="link-container">
                  <el-input :value="currentPageUrl" readonly />
                  <el-button type="primary" @click="copyLink">
                    <el-icon><DocumentCopy /></el-icon>
                    复制
                  </el-button>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="二维码" name="qr">
              <div class="share-qr-section">
                <div class="qr-container">
                  <QRCodeVue3
                    :value="currentPageUrl"
                    :width="200"
                    :height="200"
                    :imageOptions="{
                      hideBackgroundDots: false,
                      imageSize: 0.4,
                      margin: 0
                    }"
                  />
                </div>
                <p class="qr-tip">扫描二维码分享给朋友</p>
              </div>
            </el-tab-pane>

            <el-tab-pane label="社交媒体" name="social">
              <div class="social-share">
                <div class="social-buttons">
                  <el-button class="social-btn wechat">
                    <el-icon><ChatDotSquare /></el-icon>
                    微信
                  </el-button>
                  <el-button class="social-btn weibo">
                    <el-icon><Share /></el-icon>
                    微博
                  </el-button>
                  <el-button class="social-btn qq">
                    <el-icon><ChatSquare /></el-icon>
                    QQ空间
                  </el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import {
  ArrowLeft,
  Share,
  Clock,
  User,
  MapLocation,
  Guide,
  LocationFilled,
  Right,
  Ticket,
  ShoppingCart,
  Star,
  Warning,
  DocumentCopy,
  ChatDotSquare,
  ChatSquare
} from "@element-plus/icons-vue";
import QRCodeVue3 from "qrcode-vue3";
import useClipboard from "vue-clipboard3";
import { getSpotRouteVoByIdUsingGet } from "@/api/spotRouteController";
import RouteMap from "@/components/RouteMap/index.vue";

const route = useRoute();
const { toClipboard } = useClipboard();

// 路线ID
const routeId = Number(route.params.id as string);

// 分享对话框
const shareDialogVisible = ref(false);
const shareActiveTab = ref("link");
const currentPageUrl = ref(window.location.href);

// 路线信息
const routeInfo = ref({
  title: "",
  spotNameList: [] as string[],
  spotDistanceList: [] as number[],
  adminId: 0,
  spotRouteAvatar: "",
  spotRouteDescription: "",
  createTime: "",
  difficulty: "",
  duration: "",
  rating: 0,
  participants: 0,
  price: 0
});

// 相关推荐路线
const relatedRoutes = ref([
  {
    id: 1,
    title: "北京经典三日游",
    spots: 8,
    price: 1299,
    image: require("@/assets/images/spot/photo1.png")
  },
  {
    id: 2,
    title: "上海都市风光游",
    spots: 6,
    price: 999,
    image: require("@/assets/images/spot/photo2.png")
  },
  {
    id: 3,
    title: "西安古都文化游",
    spots: 7,
    price: 1199,
    image: require("@/assets/images/spot/photo3.png")
  }
]);

// 计算属性
const totalDistance = computed(() => {
  return routeInfo.value.spotDistanceList.reduce(
    (sum, distance) => sum + distance,
    0
  );
});

const estimatedTime = computed(() => {
  const hours = Math.ceil((totalDistance.value / 60) * 2); // 假设平均速度30km/h
  return `${hours}小时`;
});

const estimatedCost = computed(() => {
  return Math.ceil(routeInfo.value.spotNameList.length * 80); // 每个景点平均80元
});

// 获取景点图片
const getSpotImage = (index: number) => {
  const images = [
    require("@/assets/images/spot/photo1.png"),
    require("@/assets/images/spot/photo2.png"),
    require("@/assets/images/spot/photo3.png"),
    require("@/assets/images/spot/photo4.png"),
    require("@/assets/images/spot/photo5.png"),
    require("@/assets/images/spot/photo6.png")
  ];
  return images[index % images.length];
};

// 获取景点描述
const getSpotDescription = (index: number) => {
  const descriptions = [
    "历史悠久的文化古迹，值得深度游览",
    "自然风光秀丽，是摄影爱好者的天堂",
    "现代都市景观，体验城市魅力",
    "传统建筑群落，感受古典文化",
    "特色美食聚集地，品尝地道风味",
    "休闲娱乐场所，放松身心的好去处"
  ];
  return descriptions[index % descriptions.length];
};

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return "未知";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN");
};

// 获取路线详情
const fetchRouteDetail = async () => {
  try {
    // 首先尝试从localStorage获取传递过来的路线数据
    const storedRouteData = localStorage.getItem("currentRouteData");

    if (storedRouteData) {
      const routeData = JSON.parse(storedRouteData);

      // 使用传递过来的完整数据
      routeInfo.value = {
        title: routeData.title || "精品旅游路线",
        spotNameList: routeData.spotNames || [],
        spotDistanceList: generateDistances(routeData.spotNames?.length || 0),
        adminId: 1001,
        spotRouteAvatar:
          routeData.coverImage || require("@/assets/images/welcome/logo.png"),
        spotRouteDescription: routeData.description || "",
        createTime: routeData.createTime || new Date().toISOString(),
        difficulty: routeData.difficulty || "适中",
        duration: routeData.duration || "2-3天",
        rating: routeData.rating || 4.5,
        participants: routeData.participants || 1000,
        price: routeData.price || 999
      };

      // 清除localStorage中的数据（可选）
      localStorage.removeItem("currentRouteData");

      return;
    }

    // 如果没有传递的数据，则尝试从API获取
    const res = await getSpotRouteVoByIdUsingGet({ id: routeId });
    if (res.data?.code === 200 && res.data.data) {
      const data = res.data.data;
      routeInfo.value = {
        title: data.spotRouteDescription || "精品旅游路线",
        spotNameList: data.spotNameList || [],
        spotDistanceList: data.spotDistanceList || [],
        adminId: data.adminId || 0,
        spotRouteAvatar:
          data.spotRouteAvatar || require("@/assets/images/welcome/logo.png"),
        spotRouteDescription: data.spotRouteDescription || "",
        createTime: data.createTime || "",
        difficulty: "适中",
        duration: "2-3天",
        rating: 4.5,
        participants: 1000,
        price: 999
      };
    } else {
      // 如果API也失败，使用默认数据
      loadDefaultRouteData();
    }
  } catch (error) {
    console.error(error);
    // 如果出错，加载默认数据
    loadDefaultRouteData();
  }
};

// 生成距离数据的辅助函数
const generateDistances = (spotCount: number): number[] => {
  const distances: number[] = [];
  for (let i = 0; i < spotCount - 1; i++) {
    distances.push(Math.random() * 50 + 10); // 10-60km之间的随机距离
  }
  return distances;
};

// 加载默认路线数据
const loadDefaultRouteData = () => {
  routeInfo.value = {
    title: "精品旅游路线",
    spotNameList: ["景点一", "景点二", "景点三", "景点四"],
    spotDistanceList: [25.5, 18.3, 32.1],
    adminId: 1001,
    spotRouteAvatar: require("@/assets/images/welcome/logo.png"),
    spotRouteDescription:
      "这是一条精心设计的旅游路线，带您领略沿途的美景和文化。",
    createTime: new Date().toISOString(),
    difficulty: "适中",
    duration: "2-3天",
    rating: 4.5,
    participants: 1000,
    price: 999
  };
};

// 复制链接
const copyLink = async () => {
  try {
    await toClipboard(currentPageUrl.value);
    ElMessage.success("链接已复制到剪贴板");
  } catch (e) {
    ElMessage.error("复制失败");
  }
};

// 页面初始化
onMounted(() => {
  fetchRouteDetail();
});
</script>

<style scoped lang="scss">
.route-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.top-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;

  .navbar-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 16px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .nav-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .nav-title {
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }
    }
  }
}

.route-header {
  .header-bg {
    height: 300px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    overflow: hidden;

    .header-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .header-content {
      max-width: 1200px;
      width: 100%;
      padding: 0 20px;
      display: flex;
      align-items: center;
      gap: 32px;
      color: white;

      .route-avatar {
        width: 120px;
        height: 120px;
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .route-info {
        flex: 1;

        .route-title {
          font-size: 2.5rem;
          font-weight: bold;
          margin: 0 0 12px 0;
          text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
        }

        .route-subtitle {
          font-size: 1.2rem;
          margin: 0 0 20px 0;
          opacity: 0.9;
          line-height: 1.5;
        }

        .route-meta {
          display: flex;
          gap: 24px;
          flex-wrap: wrap;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 1rem;
          }
        }
      }
    }
  }
}

.main-content {
  padding: 40px 20px;

  .content-wrapper {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    gap: 32px;
  }
}

.left-content {
  flex: 2;

  .route-overview {
    margin-bottom: 24px;

    .overview-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
      gap: 16px;

      .stat-card {
        text-align: center;
        padding: 20px;
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        border-radius: 12px;
        color: white;

        .stat-number {
          font-size: 2rem;
          font-weight: bold;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 0.9rem;
          opacity: 0.9;
        }

        &:nth-child(2) {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        &:nth-child(3) {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }

        &:nth-child(4) {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
        }
      }
    }
  }

  .spots-list {
    margin-bottom: 24px;

    .spots-timeline {
      .timeline-item {
        display: flex;
        margin-bottom: 32px;

        &.last-item {
          margin-bottom: 0;

          .timeline-marker::after {
            display: none;
          }
        }

        .timeline-marker {
          position: relative;
          margin-right: 24px;

          .marker-number {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #409eff;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
          }

          &::after {
            content: "";
            position: absolute;
            top: 40px;
            left: 50%;
            transform: translateX(-50%);
            width: 2px;
            height: 60px;
            background: #e4e7ed;
          }
        }

        .timeline-content {
          flex: 1;

          .spot-card {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            display: flex;
            margin-bottom: 16px;

            .spot-image {
              width: 160px;
              height: 120px;
              position: relative;

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
              }

              .spot-badge {
                position: absolute;
                top: 8px;
                left: 8px;
                background: rgba(64, 158, 255, 0.9);
                color: white;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
              }
            }

            .spot-info {
              flex: 1;
              padding: 16px;

              .spot-name {
                margin: 0 0 8px 0;
                color: #333;
                font-size: 18px;
              }

              .spot-description {
                margin: 0 0 12px 0;
                color: #666;
                font-size: 14px;
                line-height: 1.5;
              }

              .spot-tags {
                margin-bottom: 16px;
                display: flex;
                gap: 8px;
              }

              .spot-actions {
                display: flex;
                gap: 8px;
              }
            }
          }

          .distance-info {
            .distance-line {
              display: flex;
              align-items: center;
              gap: 8px;
              color: #999;
              font-size: 14px;
              padding: 8px 16px;
              background: #f8f9fa;
              border-radius: 8px;

              .travel-time {
                color: #67c23a;
                font-weight: 500;
              }
            }
          }
        }
      }
    }
  }

  .route-map {
    .map-content {
      height: 400px;
      border-radius: 8px;
      overflow: hidden;
    }
  }
}

.right-sidebar {
  flex: 1;

  .quick-actions {
    margin-bottom: 24px;

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .action-btn {
        width: 100%;
        height: 48px;
        font-size: 16px;
        font-weight: 500;
      }
    }
  }

  .route-details {
    margin-bottom: 24px;

    .detail-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .detail-label {
        color: #666;
        font-size: 14px;
      }

      .detail-value {
        color: #333;
        font-weight: 500;
      }
    }
  }

  .tips-card {
    margin-bottom: 24px;

    .tips-list {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;
        color: #666;
        font-size: 14px;
        line-height: 1.5;

        &:last-child {
          border-bottom: none;
        }

        &::before {
          content: "•";
          color: #409eff;
          margin-right: 8px;
        }
      }
    }
  }

  .recommendations {
    .recommend-list {
      .recommend-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .recommend-image {
          width: 60px;
          height: 60px;
          border-radius: 8px;
          object-fit: cover;
        }

        .recommend-info {
          flex: 1;

          h5 {
            margin: 0 0 4px 0;
            color: #333;
            font-size: 14px;
          }

          p {
            margin: 0 0 4px 0;
            color: #999;
            font-size: 12px;
          }

          .recommend-price {
            color: #ff6b35;
            font-weight: bold;
            font-size: 14px;
          }
        }
      }
    }
  }
}

.share-dialog-content {
  .share-preview {
    .preview-header {
      display: flex;
      align-items: center;
      gap: 16px;

      .preview-avatar {
        width: 80px;
        height: 80px;
        border-radius: 8px;
        object-fit: cover;
      }

      .preview-info {
        flex: 1;

        h4 {
          margin: 0 0 8px 0;
          color: #333;
        }

        p {
          margin: 0 0 12px 0;
          color: #666;
          font-size: 14px;
        }

        .preview-spots {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
          align-items: center;
        }
      }
    }
  }

  .share-options {
    .share-link-section {
      .link-container {
        display: flex;
        gap: 12px;
      }
    }

    .share-qr-section {
      text-align: center;

      .qr-container {
        display: inline-block;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 8px;
        margin-bottom: 16px;
      }

      .qr-tip {
        color: #666;
        font-size: 14px;
        margin: 0;
      }
    }

    .social-share {
      .social-buttons {
        display: flex;
        gap: 12px;
        justify-content: center;

        .social-btn {
          flex: 1;
          height: 48px;

          &.wechat {
            background: #07c160;
            border-color: #07c160;
            color: white;
          }

          &.weibo {
            background: #e6162d;
            border-color: #e6162d;
            color: white;
          }

          &.qq {
            background: #12b7f5;
            border-color: #12b7f5;
            color: white;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 20px !important;

    .route-title {
      font-size: 2rem !important;
    }

    .route-meta {
      justify-content: center;
      flex-direction: column;
      gap: 12px !important;
    }
  }

  .content-wrapper {
    flex-direction: column;
    gap: 24px !important;
  }

  .overview-stats {
    grid-template-columns: repeat(2, 1fr) !important;
  }

  .spot-card {
    flex-direction: column !important;

    .spot-image {
      width: 100% !important;
      height: 200px !important;
    }
  }
}
</style>
