<template>
  <div class="spot-detail-page">
    <!-- 英雄区域 -->
    <div class="hero-section">
      <div
        class="hero-background"
        :style="{ backgroundImage: `url(${spotDetail.image})` }"
      >
        <div class="hero-overlay"></div>
        <div class="hero-content">
          <!-- 面包屑导航 -->
          <div class="breadcrumb-nav">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item
                @click="$router.push('/user/spot')"
                class="breadcrumb-link"
              >
                景点列表
              </el-breadcrumb-item>
              <el-breadcrumb-item>{{ spotDetail.name }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <!-- 景点标题和基本信息 -->
          <div class="hero-info">
            <h1 class="spot-title">{{ spotDetail.name }}</h1>
            <div class="spot-location">
              <el-icon><MapLocation /></el-icon>
              <span>{{ spotDetail.location }}</span>
            </div>
            <div class="hero-stats">
              <div class="stat-item">
                <el-rate
                  :model-value="spotDetail.rating"
                  disabled
                  show-score
                  text-color="#FFD700"
                  score-template="{value}分"
                />
                <span class="review-count">({{ reviewCount }}条评价)</span>
              </div>
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ viewCount }}次浏览</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span>{{ favoriteCount }}人收藏</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <div class="content-layout">
        <!-- 左侧主要内容 -->
        <div class="main-content">
          <!-- 景点特色标签 -->
          <div class="features-section">
            <h3 class="section-title">景点特色</h3>
            <div class="features-tags">
              <el-tag
                v-for="(feature, index) in spotDetail.features"
                :key="index"
                :type="getTagType(index)"
                size="large"
                class="feature-tag"
              >
                <el-icon><Star /></el-icon>
                {{ feature }}
              </el-tag>
            </div>
          </div>

          <!-- 详情选项卡 -->
          <div class="detail-tabs-container">
            <el-tabs v-model="activeTab" class="detail-tabs" type="card">
              <!-- 景点介绍 -->
              <el-tab-pane name="intro">
                <template #label>
                  <span class="tab-label">
                    <el-icon><Document /></el-icon>
                    景点介绍
                  </span>
                </template>
                <div class="tab-content intro-content">
                  <div class="intro-description">
                    <p>{{ spotDetail.description }}</p>
                  </div>
                  <div class="intro-highlights">
                    <h4>景点亮点</h4>
                    <div class="highlights-grid">
                      <div class="highlight-item">
                        <el-icon class="highlight-icon"><Trophy /></el-icon>
                        <div>
                          <h5>世界文化遗产</h5>
                          <p>联合国教科文组织认定的世界文化遗产</p>
                        </div>
                      </div>
                      <div class="highlight-item">
                        <el-icon class="highlight-icon"><Medal /></el-icon>
                        <div>
                          <h5>5A级景区</h5>
                          <p>国家旅游局评定的最高等级旅游景区</p>
                        </div>
                      </div>
                      <div class="highlight-item">
                        <el-icon class="highlight-icon"><Microphone /></el-icon>
                        <div>
                          <h5>专业讲解</h5>
                          <p>提供专业导游讲解服务，深度了解历史文化</p>
                        </div>
                      </div>
                      <div class="highlight-item">
                        <el-icon class="highlight-icon"><Camera /></el-icon>
                        <div>
                          <h5>网红打卡</h5>
                          <p>热门拍照打卡地，记录美好旅行时光</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <!-- 门票信息 -->
              <el-tab-pane name="tickets">
                <template #label>
                  <span class="tab-label">
                    <el-icon><Ticket /></el-icon>
                    门票信息
                  </span>
                </template>
                <div class="tab-content tickets-content">
                  <div class="ticket-types">
                    <div class="ticket-item">
                      <div class="ticket-info">
                        <h4>成人票</h4>
                        <p>适用于18-60岁成人游客</p>
                        <div class="ticket-features">
                          <span class="feature">免费退改</span>
                          <span class="feature">快速入园</span>
                        </div>
                      </div>
                      <div class="ticket-price">
                        <span class="current-price"
                          >¥{{ spotDetail.price }}</span
                        >
                        <span
                          class="original-price"
                          v-if="spotDetail.originalPrice"
                          >¥{{ spotDetail.originalPrice }}</span
                        >
                      </div>
                    </div>
                    <div class="ticket-item">
                      <div class="ticket-info">
                        <h4>学生票</h4>
                        <p>适用于在校学生（需出示学生证）</p>
                        <div class="ticket-features">
                          <span class="feature">免费退改</span>
                          <span class="feature">学生优惠</span>
                        </div>
                      </div>
                      <div class="ticket-price">
                        <span class="current-price"
                          >¥{{ Math.floor(spotDetail.price * 0.8) }}</span
                        >
                      </div>
                    </div>
                    <div class="ticket-item">
                      <div class="ticket-info">
                        <h4>儿童票</h4>
                        <p>适用于6-18岁儿童（需出示身份证件）</p>
                        <div class="ticket-features">
                          <span class="feature">免费退改</span>
                          <span class="feature">儿童优惠</span>
                        </div>
                      </div>
                      <div class="ticket-price">
                        <span class="current-price"
                          >¥{{ Math.floor(spotDetail.price * 0.6) }}</span
                        >
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <!-- 游客评价 -->
              <el-tab-pane name="reviews">
                <template #label>
                  <span class="tab-label">
                    <el-icon><ChatDotRound /></el-icon>
                    游客评价
                  </span>
                </template>
                <div class="tab-content reviews-content">
                  <div class="reviews-summary">
                    <div class="rating-overview">
                      <div class="rating-score">{{ spotDetail.rating }}</div>
                      <div class="rating-details">
                        <el-rate :model-value="spotDetail.rating" disabled />
                        <p>基于{{ reviewCount }}条真实评价</p>
                      </div>
                    </div>
                    <div class="rating-distribution">
                      <div class="rating-bar" v-for="i in 5" :key="i">
                        <span>{{ 6 - i }}星</span>
                        <el-progress
                          :percentage="getRatingPercentage(6 - i)"
                          :show-text="false"
                          :stroke-width="8"
                        />
                        <span>{{ getRatingCount(6 - i) }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="reviews-list">
                    <div
                      v-for="review in reviews"
                      :key="review.id"
                      class="review-item"
                    >
                      <div class="reviewer-info">
                        <el-avatar
                          :src="review.avatar"
                          :alt="review.username"
                        />
                        <div class="reviewer-details">
                          <h5>{{ review.username }}</h5>
                          <div class="review-meta">
                            <el-rate
                              :model-value="review.rating"
                              disabled
                              size="small"
                            />
                            <span class="review-date">{{ review.date }}</span>
                          </div>
                        </div>
                      </div>
                      <div class="review-content">
                        <p>{{ review.content }}</p>
                        <div class="review-images" v-if="review.images">
                          <img
                            v-for="img in review.images"
                            :key="img"
                            :src="img"
                            class="review-image"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>

              <!-- 周边推荐 -->
              <el-tab-pane name="nearby">
                <template #label>
                  <span class="tab-label">
                    <el-icon><LocationFilled /></el-icon>
                    周边推荐
                  </span>
                </template>
                <div class="tab-content nearby-content">
                  <div class="nearby-spots">
                    <div
                      v-for="spot in nearbySpots"
                      :key="spot.id"
                      class="nearby-item"
                    >
                      <img
                        :src="spot.image"
                        :alt="spot.name"
                        class="nearby-image"
                      />
                      <div class="nearby-info">
                        <h5>{{ spot.name }}</h5>
                        <p class="nearby-distance">
                          <el-icon><LocationFilled /></el-icon>
                          距离{{ spot.distance }}
                        </p>
                        <div class="nearby-rating">
                          <el-rate
                            :model-value="spot.rating"
                            disabled
                            size="small"
                          />
                          <span class="nearby-price">¥{{ spot.price }}起</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 右侧操作栏 -->
        <div class="sidebar">
          <!-- 主要操作按钮 -->
          <el-card class="action-card">
            <div class="price-display">
              <div class="price-label">门票价格</div>
              <div class="price-info">
                <span class="current-price">¥{{ spotDetail.price }}</span>
                <span class="price-unit">起</span>
              </div>
              <div class="original-price" v-if="spotDetail.originalPrice">
                原价：¥{{ spotDetail.originalPrice }}
              </div>
            </div>

            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                @click="handleBooking"
                class="primary-action"
              >
                <el-icon><Ticket /></el-icon>
                立即预订
              </el-button>

              <el-button
                type="success"
                size="large"
                @click="addToCart"
                class="secondary-action"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>

              <div class="action-row">
                <el-button
                  :type="isFavorited ? 'warning' : 'default'"
                  @click="handleFavorite"
                  class="action-btn"
                >
                  <el-icon><Star /></el-icon>
                  {{ isFavorited ? "已收藏" : "收藏" }}
                </el-button>

                <el-button @click="handleShare" class="action-btn">
                  <el-icon><Share /></el-icon>
                  分享
                </el-button>
              </div>
            </div>
          </el-card>

          <!-- 景点信息卡片 -->
          <el-card class="info-card">
            <template #header>
              <h4>
                <el-icon><InfoFilled /></el-icon>
                景点信息
              </h4>
            </template>
            <div class="info-list">
              <div class="info-item">
                <el-icon class="info-icon"><Clock /></el-icon>
                <div class="info-content">
                  <span class="info-label">开放时间</span>
                  <span class="info-value">08:00 - 18:00</span>
                </div>
              </div>
              <div class="info-item">
                <el-icon class="info-icon"><Timer /></el-icon>
                <div class="info-content">
                  <span class="info-label">建议游玩</span>
                  <span class="info-value">2-3小时</span>
                </div>
              </div>
              <div class="info-item">
                <el-icon class="info-icon"><Sunny /></el-icon>
                <div class="info-content">
                  <span class="info-label">最佳季节</span>
                  <span class="info-value">四季皆宜</span>
                </div>
              </div>
              <div class="info-item">
                <el-icon class="info-icon"><Phone /></el-icon>
                <div class="info-content">
                  <span class="info-label">联系电话</span>
                  <span class="info-value">400-123-4567</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 贴心提示 -->
          <el-card class="tips-card">
            <template #header>
              <h4>
                <el-icon><Warning /></el-icon>
                贴心提示
              </h4>
            </template>
            <div class="tips-list">
              <div class="tip-item">
                <el-icon class="tip-icon"><Check /></el-icon>
                <span>建议提前1-2天预订门票</span>
              </div>
              <div class="tip-item">
                <el-icon class="tip-icon"><Check /></el-icon>
                <span>请携带有效身份证件</span>
              </div>
              <div class="tip-item">
                <el-icon class="tip-icon"><Check /></el-icon>
                <span>景区内禁止吸烟和大声喧哗</span>
              </div>
              <div class="tip-item">
                <el-icon class="tip-icon"><Check /></el-icon>
                <span>建议穿着舒适的运动鞋</span>
              </div>
            </div>
          </el-card>

          <!-- 分享功能 -->
          <el-card class="share-card" v-if="showShareCard">
            <template #header>
              <h4>
                <el-icon><Share /></el-icon>
                分享景点
              </h4>
            </template>
            <div class="share-options">
              <div class="share-item" @click="shareToWeChat">
                <el-icon><ChatRound /></el-icon>
                <span>微信</span>
              </div>
              <div class="share-item" @click="shareToWeibo">
                <el-icon><Share /></el-icon>
                <span>微博</span>
              </div>
              <div class="share-item" @click="copyLink">
                <el-icon><Link /></el-icon>
                <span>复制链接</span>
              </div>
              <div class="share-item" @click="generateQRCode">
                <el-icon><Grid /></el-icon>
                <span>二维码</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import {
  MapLocation,
  View,
  Star,
  Document,
  Ticket,
  ChatDotRound,
  LocationFilled,
  ShoppingCart,
  Share,
  InfoFilled,
  Clock,
  Timer,
  Sunny,
  Phone,
  Warning,
  Check,
  Trophy,
  Medal,
  Microphone,
  Camera,
  ChatRound,
  Link,
  Grid
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();

// 响应式数据
const activeTab = ref("intro");
const isFavorited = ref(false);
const showShareCard = ref(false);
const viewCount = ref(12580);
const favoriteCount = ref(3240);
const reviewCount = ref(856);

// 景点详情数据
const spotDetail = ref({
  id: 1,
  name: "故宫博物院",
  image: require("@/assets/images/spot/photo1.png"),
  location: "北京市东城区景山前街4号",
  rating: 4.8,
  price: 60,
  originalPrice: 80,
  description:
    "北京故宫是中国明清两代的皇家宫殿，旧称紫禁城，位于北京中轴线的中心。北京故宫以三大殿为中心，占地面积约72万平方米，建筑面积约15万平方米，有大小宫殿七十多座，房屋九千余间。北京故宫于明成祖永乐四年（1406年）开始建设，以南京故宫为蓝本营建，到永乐十八年（1420年）建成，成为明清两朝二十四位皇帝的皇宫。",
  features: [
    "5A景区",
    "世界遗产",
    "免费讲解",
    "网红打卡",
    "历史文化",
    "古建筑群"
  ]
});

// 评价数据
const reviews = ref([
  {
    id: 1,
    username: "旅行达人小王",
    avatar:
      "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
    rating: 5,
    date: "2024-01-15",
    content:
      "故宫真的太震撼了！建筑宏伟，历史厚重，每一个角落都充满了故事。导游讲解很专业，让我对中国古代文化有了更深的了解。强烈推荐！",
    images: [require("@/assets/images/spot/photo1.png")]
  },
  {
    id: 2,
    username: "摄影爱好者",
    avatar:
      "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    rating: 4,
    date: "2024-01-10",
    content:
      "拍照圣地！红墙黄瓦，古色古香，每一张照片都是大片。建议早上去，人少光线好。",
    images: []
  },
  {
    id: 3,
    username: "历史文化爱好者",
    avatar:
      "https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png",
    rating: 5,
    date: "2024-01-08",
    content:
      "作为中华文明的瑰宝，故宫承载着深厚的历史文化底蕴。每次来都有新的发现和感悟。",
    images: []
  }
]);

// 周边推荐景点
const nearbySpots = ref([
  {
    id: 2,
    name: "天安门广场",
    image: require("@/assets/images/spot/photo2.png"),
    distance: "0.5km",
    rating: 4.7,
    price: 0
  },
  {
    id: 3,
    name: "天坛公园",
    image: require("@/assets/images/spot/photo8.png"),
    distance: "2.1km",
    rating: 4.6,
    price: 15
  },
  {
    id: 4,
    name: "颐和园",
    image: require("@/assets/images/spot/photo3.png"),
    distance: "12.8km",
    rating: 4.8,
    price: 30
  }
]);

// 计算属性和方法
const getTagType = (index: number) => {
  const types = ["primary", "success", "warning", "danger", "info"];
  return types[index % types.length];
};

const getRatingPercentage = (stars: number) => {
  const distribution = { 5: 60, 4: 25, 3: 10, 2: 3, 1: 2 };
  return distribution[stars as keyof typeof distribution] || 0;
};

const getRatingCount = (stars: number) => {
  const total = reviewCount.value;
  const percentage = getRatingPercentage(stars);
  return Math.floor((total * percentage) / 100);
};

// 事件处理方法
const handleBooking = () => {
  router.push({
    path: "/user/payment",
    query: {
      spotId: spotDetail.value.id,
      spotName: spotDetail.value.name,
      spotPrice: spotDetail.value.price,
      spotImage: spotDetail.value.image,
      spotLocation: spotDetail.value.location,
      spotDescription: spotDetail.value.description
    }
  });
};

const addToCart = () => {
  const existingCart = JSON.parse(localStorage.getItem("tourismCart") || "[]");

  const existingIndex = existingCart.findIndex(
    (item: any) => item.id === spotDetail.value.id && item.type === "spot"
  );

  if (existingIndex >= 0) {
    existingCart[existingIndex].quantity += 1;
  } else {
    const newItem = {
      id: spotDetail.value.id,
      type: "spot",
      name: spotDetail.value.name,
      price: spotDetail.value.price,
      image: spotDetail.value.image,
      location: spotDetail.value.location,
      description: spotDetail.value.description,
      quantity: 1,
      visitors: [
        {
          name: "",
          phone: "",
          idCard: "",
          visitDate: null
        }
      ]
    };
    existingCart.push(newItem);
  }

  localStorage.setItem("tourismCart", JSON.stringify(existingCart));
  ElMessage.success("已添加到购物车");
};

const handleFavorite = async () => {
  try {
    isFavorited.value = !isFavorited.value;
    if (isFavorited.value) {
      favoriteCount.value += 1;
      ElMessage.success("收藏成功");
    } else {
      favoriteCount.value -= 1;
      ElMessage.success("取消收藏");
    }
  } catch (error) {
    ElMessage.error("操作失败，请重试");
  }
};

const handleShare = () => {
  showShareCard.value = !showShareCard.value;
};

const shareToWeChat = () => {
  ElMessage.success("分享到微信");
};

const shareToWeibo = () => {
  ElMessage.success("分享到微博");
};

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href);
    ElMessage.success("链接已复制到剪贴板");
  } catch (error) {
    ElMessage.error("复制失败");
  }
};

const generateQRCode = () => {
  ElMessage.success("二维码生成中...");
};

// 生命周期
onMounted(() => {
  console.log("景点详情页面已挂载");
  console.log("当前路由:", route.path);
  console.log("路由参数:", route.params);

  // 获取路由参数
  const spotId = route.params.id;
  console.log("景点ID:", spotId);

  // 从localStorage获取景点详情数据
  const storedSpotDetail = localStorage.getItem("currentSpotDetail");
  if (storedSpotDetail) {
    try {
      const parsedData = JSON.parse(storedSpotDetail);
      spotDetail.value = {
        ...spotDetail.value,
        ...parsedData
      };
      console.log("景点详情数据:", spotDetail.value);
    } catch (error) {
      console.error("解析景点数据失败:", error);
    }
  } else {
    console.warn("未找到景点详情数据，使用默认数据");
  }

  // 增加浏览量
  viewCount.value += 1;

  // 检查是否已收藏
  const favorites = JSON.parse(localStorage.getItem("userFavorites") || "[]");
  isFavorited.value = favorites.includes(spotDetail.value.id);
});
</script>

<style lang="scss" scoped>
.spot-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.hero-section {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.hero-background {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.2));
}

.hero-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 32px;
  color: white;
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb-nav {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: rgba(255, 255, 255, 0.8);

      &:hover {
        color: white;
      }
    }
  }
}

.breadcrumb-link {
  cursor: pointer;
}

.hero-info {
  .spot-title {
    font-size: 48px;
    font-weight: 700;
    margin: 0 0 16px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  .spot-location {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    margin-bottom: 24px;
    opacity: 0.9;
  }
}

.hero-stats {
  display: flex;
  gap: 32px;
  align-items: center;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;

    .review-count {
      opacity: 0.8;
      margin-left: 8px;
    }
  }
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

.content-layout {
  display: flex;
  gap: 32px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.features-section {
  margin-bottom: 32px;

  .section-title {
    font-size: 24px;
    font-weight: 600;
    color: #1a202c;
    margin: 0 0 16px 0;
    border-left: 4px solid #667eea;
    padding-left: 12px;
  }
}

.features-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.feature-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
}

.detail-tabs-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.detail-tabs {
  :deep(.el-tabs__header) {
    margin: 0;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0 24px;
  }

  :deep(.el-tabs__item) {
    padding: 16px 24px;
    font-weight: 500;

    &.is-active {
      background: white;
      border-bottom-color: white;
    }
  }
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-content {
  padding: 32px;
}

.intro-content {
  .intro-description {
    font-size: 16px;
    line-height: 1.8;
    color: #4a5568;
    margin-bottom: 32px;
  }

  .intro-highlights {
    h4 {
      font-size: 20px;
      font-weight: 600;
      color: #2d3748;
      margin: 0 0 20px 0;
    }
  }
}

.highlights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.highlight-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #f7fafc;
  border-radius: 12px;

  .highlight-icon {
    font-size: 24px;
    color: #667eea;
    margin-top: 4px;
  }

  h5 {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: #2d3748;
  }

  p {
    margin: 0;
    color: #718096;
    line-height: 1.5;
  }
}

.tickets-content {
  .ticket-types {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .ticket-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    transition: all 0.3s ease;

    &:hover {
      border-color: #667eea;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
    }
  }

  .ticket-info {
    h4 {
      margin: 0 0 8px 0;
      font-size: 18px;
      font-weight: 600;
      color: #2d3748;
    }

    p {
      margin: 0 0 12px 0;
      color: #718096;
    }
  }

  .ticket-features {
    display: flex;
    gap: 8px;

    .feature {
      padding: 4px 8px;
      background: #e6fffa;
      color: #319795;
      border-radius: 4px;
      font-size: 12px;
    }
  }

  .ticket-price {
    text-align: right;

    .current-price {
      font-size: 24px;
      font-weight: 700;
      color: #e53e3e;
    }

    .original-price {
      display: block;
      font-size: 14px;
      color: #a0aec0;
      text-decoration: line-through;
      margin-top: 4px;
    }
  }
}

.reviews-content {
  .reviews-summary {
    display: flex;
    gap: 32px;
    margin-bottom: 32px;
    padding: 24px;
    background: #f7fafc;
    border-radius: 12px;
  }

  .rating-overview {
    display: flex;
    align-items: center;
    gap: 16px;

    .rating-score {
      font-size: 48px;
      font-weight: 700;
      color: #2d3748;
    }

    .rating-details {
      p {
        margin: 8px 0 0 0;
        color: #718096;
      }
    }
  }

  .rating-distribution {
    flex: 1;

    .rating-bar {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;

      span:first-child {
        width: 40px;
        font-size: 14px;
        color: #718096;
      }

      span:last-child {
        width: 40px;
        text-align: right;
        font-size: 14px;
        color: #718096;
      }
    }
  }
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.review-item {
  padding: 24px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;

  .reviewer-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .reviewer-details {
      h5 {
        margin: 0 0 4px 0;
        font-weight: 600;
        color: #2d3748;
      }
    }

    .review-meta {
      display: flex;
      align-items: center;
      gap: 12px;

      .review-date {
        color: #a0aec0;
        font-size: 14px;
      }
    }
  }

  .review-content {
    p {
      margin: 0 0 16px 0;
      line-height: 1.6;
      color: #4a5568;
    }
  }

  .review-images {
    display: flex;
    gap: 8px;

    .review-image {
      width: 80px;
      height: 80px;
      object-fit: cover;
      border-radius: 8px;
    }
  }
}

.nearby-content {
  .nearby-spots {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }

  .nearby-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    transition: all 0.3s ease;
    cursor: pointer;

    &:hover {
      border-color: #667eea;
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
    }
  }

  .nearby-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
  }

  .nearby-info {
    flex: 1;

    h5 {
      margin: 0 0 8px 0;
      font-weight: 600;
      color: #2d3748;
    }

    .nearby-distance {
      display: flex;
      align-items: center;
      gap: 4px;
      margin: 0 0 8px 0;
      color: #718096;
      font-size: 14px;
    }

    .nearby-rating {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .nearby-price {
        font-weight: 600;
        color: #e53e3e;
      }
    }
  }
}

.sidebar {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.action-card {
  .price-display {
    text-align: center;
    margin-bottom: 24px;
    padding: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    color: white;

    .price-label {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 8px;
    }

    .price-info {
      display: flex;
      align-items: baseline;
      justify-content: center;
      gap: 4px;

      .current-price {
        font-size: 32px;
        font-weight: 700;
      }

      .price-unit {
        font-size: 16px;
        opacity: 0.8;
      }
    }

    .original-price {
      font-size: 14px;
      opacity: 0.7;
      text-decoration: line-through;
      margin-top: 4px;
    }
  }
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .primary-action,
  .secondary-action {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 8px;

    .el-icon {
      margin-right: 8px;
    }
  }

  .primary-action {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }

  .action-row {
    display: flex;
    gap: 12px;

    .action-btn {
      flex: 1;
      height: 40px;
    }
  }
}

.info-card,
.tips-card,
.share-card {
  h4 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #2d3748;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.info-list,
.tips-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;

  .info-icon {
    color: #667eea;
    font-size: 18px;
  }

  .info-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .info-label {
      color: #718096;
      font-size: 14px;
    }

    .info-value {
      font-weight: 500;
      color: #2d3748;
    }
  }
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;

  .tip-icon {
    color: #48bb78;
    font-size: 16px;
  }

  span {
    color: #4a5568;
    font-size: 14px;
  }
}

.share-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #667eea;
    background: #f7fafc;
  }

  .el-icon {
    font-size: 24px;
    color: #667eea;
  }

  span {
    font-size: 14px;
    color: #4a5568;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-content {
    padding: 16px;

    .spot-title {
      font-size: 32px;
    }

    .hero-stats {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }
  }

  .content-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .highlights-grid {
    grid-template-columns: 1fr;
  }

  .reviews-summary {
    flex-direction: column;
    gap: 20px;
  }

  .nearby-spots {
    grid-template-columns: 1fr;
  }
}

// Element Plus 组件样式覆盖
:deep(.el-rate) {
  .el-rate__icon {
    font-size: 18px;
  }
}

:deep(.el-progress-bar__outer) {
  background-color: #e2e8f0;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-card) {
  border-radius: 12px;
  border: 1px solid #e2e8f0;

  .el-card__header {
    border-bottom: 1px solid #e2e8f0;
  }
}
</style>
