<template>
  <div class="route-list-container">
    <!-- 顶部横幅 -->
    <div class="hero-banner">
      <div class="banner-content">
        <h1 class="banner-title">精品旅游路线</h1>
        <p class="banner-subtitle">发现最美的旅行线路，开启难忘的旅程</p>
        <div class="banner-stats">
          <div class="stat-item">
            <span class="stat-number">{{ totalRoutes }}</span>
            <span class="stat-label">精选路线</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ totalSpots }}</span>
            <span class="stat-label">覆盖景点</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ totalUsers }}</span>
            <span class="stat-label">用户体验</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选栏 -->
    <div class="search-filter-bar">
      <div class="search-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索路线名称或景点..."
          size="large"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" size="large" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
      <div class="filter-tags">
        <el-tag
          v-for="tag in filterTags"
          :key="tag"
          :type="selectedTag === tag ? 'primary' : 'info'"
          @click="handleTagFilter(tag)"
          class="filter-tag"
          :effect="selectedTag === tag ? 'dark' : 'plain'"
        >
          {{ tag }}
        </el-tag>
      </div>
    </div>

    <!-- 路线卡片列表 -->
    <div class="routes-container">
      <div class="routes-grid">
        <div
          v-for="route in displayRoutes"
          :key="route.id"
          class="route-card"
          @click="goRouteDetail(route.id)"
        >
          <div class="card-image">
            <img :src="route.coverImage" :alt="route.title" />
            <div class="image-overlay">
              <div class="route-badge">{{ route.difficulty }}</div>
              <div class="duration-badge">{{ route.duration }}</div>
            </div>
          </div>

          <div class="card-content">
            <div class="route-header">
              <h3 class="route-title">{{ route.title }}</h3>
              <div class="route-rating">
                <el-rate :model-value="route.rating" disabled size="small" />
                <span class="rating-score">{{ route.rating }}</span>
              </div>
            </div>

            <p class="route-description">{{ route.description }}</p>

            <div class="route-spots">
              <div class="spots-preview">
                <el-icon><LocationFilled /></el-icon>
                <span class="spots-text">
                  {{ route.spotNames.slice(0, 3).join(" → ") }}
                  <span v-if="route.spotNames.length > 3">...</span>
                </span>
              </div>
              <div class="spots-count">{{ route.spotNames.length }}个景点</div>
            </div>

            <div class="route-meta">
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>{{ route.duration }}</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ route.participants }}人已体验</span>
              </div>
              <div class="meta-item">
                <el-icon><MapLocation /></el-icon>
                <span>{{ route.distance }}km</span>
              </div>
            </div>

            <div class="route-footer">
              <div class="price-section">
                <span class="price-label">参考价格</span>
                <span class="price-value">¥{{ route.price }}</span>
                <span class="price-unit">起/人</span>
              </div>
              <el-button type="primary" class="view-button">
                查看详情
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more-section">
        <el-button
          type="primary"
          size="large"
          @click="loadMore"
          :loading="loading"
          class="load-more-btn"
        >
          <el-icon v-if="!loading"><Plus /></el-icon>
          {{ loading ? "加载中..." : "加载更多路线" }}
        </el-button>
      </div>
    </div>

    <!-- 底部推荐 -->
    <div class="recommendation-section">
      <h2 class="section-title">热门推荐</h2>
      <div class="recommendation-grid">
        <div
          v-for="item in recommendations"
          :key="item.id"
          class="recommendation-card"
        >
          <img :src="item.image" :alt="item.title" class="recommend-image" />
          <div class="recommend-content">
            <h4>{{ item.title }}</h4>
            <p>{{ item.description }}</p>
            <div class="recommend-price">¥{{ item.price }}起</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  Search,
  LocationFilled,
  Clock,
  User,
  MapLocation,
  ArrowRight,
  Plus
} from "@element-plus/icons-vue";
// import { listSpotRouteVoByPageUsingPost } from "@/api/spotRouteController";
import { ElMessage } from "element-plus";

const router = useRouter();

// 搜索和筛选
const searchKeyword = ref("");
const selectedTag = ref("全部");
const filterTags = [
  "全部",
  "经典路线",
  "自然风光",
  "文化古迹",
  "休闲度假",
  "探险刺激"
];

// 分页数据
const currentPage = ref(1);
const pageSize = ref(12);
const loading = ref(false);
const hasMore = ref(true);

// 统计数据
const totalRoutes = ref(156);
const totalSpots = ref(892);
const totalUsers = ref(15420);

// 示例路线数据
const sampleRoutes = [
  {
    id: 1,
    title: "北京经典文化之旅",
    description: "探访千年古都的历史文化遗迹，感受中华文明的博大精深",
    coverImage: require("@/assets/images/spot/photo1.png"),
    spotNames: ["天安门广场", "故宫博物院", "天坛公园", "颐和园", "长城"],
    duration: "3天2夜",
    difficulty: "轻松",
    rating: 4.8,
    participants: 2340,
    distance: 85,
    price: 1299,
    tags: ["经典路线", "文化古迹"],
    createTime: "2024-01-15"
  },
  {
    id: 2,
    title: "桂林山水甲天下",
    description: "漓江山水如画，感受大自然的鬼斧神工之美",
    coverImage: require("@/assets/images/spot/photo2.png"),
    spotNames: ["漓江", "象鼻山", "阳朔西街", "遇龙河", "银子岩"],
    duration: "4天3夜",
    difficulty: "适中",
    rating: 4.9,
    participants: 1876,
    distance: 120,
    price: 1599,
    tags: ["自然风光", "休闲度假"],
    createTime: "2024-01-10"
  },
  {
    id: 3,
    title: "西安古都探秘游",
    description: "穿越千年时光，探寻丝绸之路的起点",
    coverImage: require("@/assets/images/spot/photo3.png"),
    spotNames: ["兵马俑", "华清宫", "大雁塔", "古城墙", "回民街"],
    duration: "2天1夜",
    difficulty: "轻松",
    rating: 4.7,
    participants: 3120,
    distance: 45,
    price: 899,
    tags: ["经典路线", "文化古迹"],
    createTime: "2024-01-08"
  },
  {
    id: 4,
    title: "张家界奇峰异石之旅",
    description: "阿凡达取景地，体验悬浮山峰的震撼",
    coverImage: require("@/assets/images/spot/photo4.png"),
    spotNames: ["张家界国家森林公园", "天门山", "黄龙洞", "金鞭溪"],
    duration: "3天2夜",
    difficulty: "挑战",
    rating: 4.6,
    participants: 1654,
    distance: 78,
    price: 1199,
    tags: ["自然风光", "探险刺激"],
    createTime: "2024-01-05"
  },
  {
    id: 5,
    title: "苏州园林雅韵游",
    description: "江南水乡的诗情画意，品味古典园林艺术",
    coverImage: require("@/assets/images/spot/photo5.png"),
    spotNames: ["拙政园", "留园", "虎丘", "平江路", "周庄古镇"],
    duration: "2天1夜",
    difficulty: "轻松",
    rating: 4.5,
    participants: 2890,
    distance: 65,
    price: 799,
    tags: ["文化古迹", "休闲度假"],
    createTime: "2024-01-03"
  },
  {
    id: 6,
    title: "三亚海岛度假游",
    description: "椰风海韵，享受热带海岛的悠闲时光",
    coverImage: require("@/assets/images/spot/photo6.png"),
    spotNames: ["亚龙湾", "天涯海角", "南山寺", "蜈支洲岛"],
    duration: "4天3夜",
    difficulty: "轻松",
    rating: 4.8,
    participants: 4560,
    distance: 95,
    price: 2199,
    tags: ["休闲度假", "自然风光"],
    createTime: "2024-01-01"
  },
  {
    id: 7,
    title: "九寨沟童话世界",
    description: "人间仙境九寨沟，五彩斑斓的高原湖泊",
    coverImage: require("@/assets/images/spot/photo7.png"),
    spotNames: ["九寨沟", "黄龙", "都江堰", "青城山"],
    duration: "5天4夜",
    difficulty: "适中",
    rating: 4.9,
    participants: 1234,
    distance: 156,
    price: 2599,
    tags: ["自然风光", "探险刺激"],
    createTime: "2023-12-28"
  },
  {
    id: 8,
    title: "丽江古城慢生活",
    description: "在古城中感受慢时光，体验纳西文化",
    coverImage: require("@/assets/images/spot/photo8.png"),
    spotNames: ["丽江古城", "玉龙雪山", "泸沽湖", "束河古镇"],
    duration: "4天3夜",
    difficulty: "适中",
    rating: 4.7,
    participants: 2100,
    distance: 134,
    price: 1899,
    tags: ["文化古迹", "休闲度假"],
    createTime: "2023-12-25"
  }
];

// 路线列表
const allRoutes = ref<any[]>([...sampleRoutes]);

// 推荐内容
const recommendations = ref([
  {
    id: 1,
    title: "特色民宿推荐",
    description: "精选当地特色住宿",
    image: require("@/assets/images/welcome/hotel_recommend_1.png"),
    price: 299
  },
  {
    id: 2,
    title: "美食探索之旅",
    description: "品味地道特色美食",
    image: require("@/assets/images/welcome/hotel_recommend_2.png"),
    price: 199
  },
  {
    id: 3,
    title: "摄影主题路线",
    description: "专为摄影爱好者定制",
    image: require("@/assets/images/welcome/hotel_recommend_3.png"),
    price: 1599
  }
]);

// 计算显示的路线
const displayRoutes = computed(() => {
  let filtered = allRoutes.value;

  // 标签筛选
  if (selectedTag.value !== "全部") {
    filtered = filtered.filter((route) =>
      route.tags.includes(selectedTag.value)
    );
  }

  // 搜索筛选
  if (searchKeyword.value) {
    filtered = filtered.filter(
      (route) =>
        route.title.includes(searchKeyword.value) ||
        route.description.includes(searchKeyword.value) ||
        route.spotNames.some((spot: string) =>
          spot.includes(searchKeyword.value)
        )
    );
  }

  return filtered.slice(0, currentPage.value * pageSize.value);
});

// 获取API路线数据
const fetchRouteList = async () => {
  try {
    loading.value = true;
    // 暂时使用示例数据，API集成后可以启用
    // const response = await listSpotRouteVoByPageUsingPost({
    //   current: currentPage.value,
    //   pageSize: pageSize.value
    // });

    // 使用示例数据
    allRoutes.value = [...sampleRoutes];

    // TODO: API集成后的数据处理逻辑
    // if (response.data?.data?.records) {
    //   const apiRoutes = response.data.data.records.map((item: any, index: number) => ({
    //     // 数据转换逻辑
    //   }));
    //   allRoutes.value = [...sampleRoutes, ...apiRoutes];
    // }
  } catch (error) {
    console.error("获取路线数据失败:", error);
    // 如果API失败，仅使用示例数据
    allRoutes.value = [...sampleRoutes];
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = () => {
  // 搜索逻辑已在computed中处理
  ElMessage.success(`搜索到 ${displayRoutes.value.length} 条相关路线`);
};

// 标签筛选
const handleTagFilter = (tag: string) => {
  selectedTag.value = tag;
};

// 加载更多
const loadMore = () => {
  if (displayRoutes.value.length >= allRoutes.value.length) {
    hasMore.value = false;
    ElMessage.info("已显示全部路线");
    return;
  }

  currentPage.value++;
  if (displayRoutes.value.length >= allRoutes.value.length) {
    hasMore.value = false;
  }
};

// 跳转到路线详情
const goRouteDetail = (id: number) => {
  // 找到对应的路线数据
  const routeData = allRoutes.value.find((route) => route.id === id);

  if (routeData) {
    // 将路线数据存储到localStorage，供详情页面使用
    localStorage.setItem("currentRouteData", JSON.stringify(routeData));

    // 跳转到详情页面
    router.push(`/user/spotRoute/detail/${id}`);
  } else {
    ElMessage.error("未找到路线信息");
  }
};

// 页面初始化
onMounted(() => {
  fetchRouteList();
});
</script>

<style scoped lang="scss">
.route-list-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.hero-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 20px;
  text-align: center;

  .banner-content {
    max-width: 1200px;
    margin: 0 auto;

    .banner-title {
      font-size: 3rem;
      font-weight: bold;
      margin: 0 0 16px 0;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
    }

    .banner-subtitle {
      font-size: 1.2rem;
      margin: 0 0 40px 0;
      opacity: 0.9;
    }

    .banner-stats {
      display: flex;
      justify-content: center;
      gap: 60px;

      .stat-item {
        text-align: center;

        .stat-number {
          display: block;
          font-size: 2.5rem;
          font-weight: bold;
          margin-bottom: 8px;
        }

        .stat-label {
          font-size: 1rem;
          opacity: 0.8;
        }
      }
    }
  }
}

.search-filter-bar {
  background: white;
  padding: 30px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .search-container {
    max-width: 800px;
    margin: 0 auto 20px auto;
    display: flex;
    gap: 16px;

    .search-input {
      flex: 1;
    }
  }

  .filter-tags {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;

    .filter-tag {
      cursor: pointer;
      transition: all 0.3s;
      padding: 8px 16px;

      &:hover {
        transform: translateY(-2px);
      }
    }
  }
}

.routes-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.routes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 32px;
  margin-bottom: 60px;
}

.route-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  }

  .card-image {
    position: relative;
    height: 220px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
    }

    &:hover img {
      transform: scale(1.05);
    }

    .image-overlay {
      position: absolute;
      top: 16px;
      left: 16px;
      right: 16px;
      display: flex;
      justify-content: space-between;

      .route-badge,
      .duration-badge {
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;
        backdrop-filter: blur(10px);
      }

      .route-badge {
        background: rgba(255, 255, 255, 0.9);
        color: #333;
      }

      .duration-badge {
        background: rgba(64, 158, 255, 0.9);
        color: white;
      }
    }
  }

  .card-content {
    padding: 24px;

    .route-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;

      .route-title {
        font-size: 1.25rem;
        font-weight: bold;
        color: #333;
        margin: 0;
        line-height: 1.4;
      }

      .route-rating {
        display: flex;
        align-items: center;
        gap: 4px;

        .rating-score {
          font-size: 14px;
          color: #666;
          font-weight: 500;
        }
      }
    }

    .route-description {
      color: #666;
      font-size: 14px;
      line-height: 1.6;
      margin: 0 0 16px 0;
    }

    .route-spots {
      margin-bottom: 16px;

      .spots-preview {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .spots-text {
          font-size: 14px;
          color: #555;
          line-height: 1.4;
        }
      }

      .spots-count {
        font-size: 12px;
        color: #999;
        padding-left: 24px;
      }
    }

    .route-meta {
      display: flex;
      gap: 16px;
      margin-bottom: 20px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #666;
      }
    }

    .route-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .price-section {
        .price-label {
          font-size: 12px;
          color: #999;
          display: block;
          margin-bottom: 4px;
        }

        .price-value {
          font-size: 20px;
          font-weight: bold;
          color: #ff6b35;
        }

        .price-unit {
          font-size: 12px;
          color: #999;
        }
      }

      .view-button {
        padding: 10px 20px;
        border-radius: 20px;
        font-weight: 500;
      }
    }
  }
}

.load-more-section {
  text-align: center;

  .load-more-btn {
    padding: 16px 32px;
    border-radius: 25px;
    font-size: 16px;
    font-weight: 500;
  }
}

.recommendation-section {
  background: white;
  padding: 60px 20px;
  margin-top: 40px;

  .section-title {
    text-align: center;
    font-size: 2rem;
    font-weight: bold;
    color: #333;
    margin: 0 0 40px 0;
  }

  .recommendation-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 24px;

    .recommendation-card {
      background: #f8f9fa;
      border-radius: 12px;
      overflow: hidden;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-4px);
      }

      .recommend-image {
        width: 100%;
        height: 160px;
        object-fit: cover;
      }

      .recommend-content {
        padding: 20px;

        h4 {
          margin: 0 0 8px 0;
          color: #333;
        }

        p {
          margin: 0 0 12px 0;
          color: #666;
          font-size: 14px;
        }

        .recommend-price {
          color: #ff6b35;
          font-weight: bold;
          font-size: 18px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .banner-title {
    font-size: 2rem !important;
  }

  .banner-stats {
    flex-direction: column;
    gap: 30px !important;
  }

  .search-container {
    flex-direction: column;
    gap: 12px !important;
  }

  .routes-grid {
    grid-template-columns: 1fr;
    gap: 20px !important;
  }

  .route-meta {
    flex-direction: column;
    gap: 8px !important;
  }

  .route-footer {
    flex-direction: column;
    gap: 16px !important;
    align-items: stretch;
  }
}
</style>
