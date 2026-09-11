<template>
  <div class="hotel-portal-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input class="search-input" placeholder="请输入城市、酒店名称" />
      <el-button type="primary" class="search-btn">搜索酒店</el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-row">
        <span class="filter-label">目的地</span>
        <span class="filter-btn active">全部</span>
        <span class="filter-btn">北京</span>
        <span class="filter-btn">上海</span>
        <span class="filter-btn">广州</span>
        <span class="filter-btn">深圳</span>
        <span class="filter-btn">成都</span>
        <span class="filter-btn">杭州</span>
        <span class="filter-btn">西安</span>
        <span class="filter-btn">青岛</span>
        <span class="filter-btn">厦门</span>
      </div>
      <div class="filter-row">
        <span class="filter-label">酒店类型</span>
        <span class="filter-btn active">全部类型</span>
        <span class="filter-btn">豪华酒店</span>
        <span class="filter-btn">商务酒店</span>
        <span class="filter-btn">度假酒店</span>
        <span class="filter-btn">精品酒店</span>
        <span class="filter-btn">民宿客栈</span>
        <span class="filter-btn">青年旅社</span>
      </div>
      <div class="filter-row">
        <span class="filter-label">排序</span>
        <span class="filter-btn active">综合排序</span>
        <span class="filter-btn">价格低-高</span>
        <span class="filter-btn">价格高-低</span>
        <span class="filter-btn">好评优先</span>
        <span class="filter-btn">距离近-远</span>
      </div>
    </div>

    <!-- Tabs标签页 -->
    <div class="tabs-container">
      <el-tabs
        v-model="activeTab"
        @tab-change="handleTabChange"
        class="hotel-tabs"
      >
        <el-tab-pane label="推荐酒店" name="recommend"></el-tab-pane>
        <el-tab-pane label="豪华酒店" name="luxury"></el-tab-pane>
        <el-tab-pane label="商务酒店" name="business"></el-tab-pane>
        <el-tab-pane label="度假酒店" name="resort"></el-tab-pane>
        <el-tab-pane label="精品民宿" name="boutique"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 主体内容区 -->
    <div class="main-content">
      <!-- 左侧酒店列表 -->
      <div class="hotel-list-section">
        <div class="hotel-list-toolbar">
          <el-checkbox>仅看有房</el-checkbox>
          <el-checkbox>免费WiFi</el-checkbox>
          <el-checkbox>免费早餐</el-checkbox>
          <el-checkbox>可取消</el-checkbox>
          <el-checkbox>五星级</el-checkbox>
          <el-checkbox>近地铁</el-checkbox>
        </div>

        <div class="hotel-list">
          <el-card
            v-for="item in currentHotelList"
            :key="item.id"
            class="hotel-card"
          >
            <div class="hotel-card-main">
              <img :src="item.image" class="hotel-img" />
              <div class="hotel-info">
                <div class="hotel-title-row">
                  <span class="hotel-title">{{ item.name }}</span>
                  <div class="hotel-stars">
                    <el-rate
                      :model-value="item.stars"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}星级"
                    />
                  </div>
                </div>
                <div class="hotel-desc">
                  {{ item.description }}
                </div>
                <div class="hotel-features">
                  <el-tag
                    v-for="feature in item.features"
                    :key="feature"
                    type="success"
                    size="small"
                  >
                    {{ feature }}
                  </el-tag>
                </div>
                <div class="hotel-location">
                  <el-icon><LocationFilled /></el-icon>
                  {{ item.location }}
                </div>
              </div>
              <div class="hotel-price">
                <div class="price-info">
                  <span class="price-label">起</span>
                  <span class="price-value">¥{{ item.price }}</span>
                  <span class="price-unit">/晚</span>
                </div>
                <div class="price-original" v-if="item.originalPrice">
                  原价：¥{{ item.originalPrice }}
                </div>
                <div class="hotel-actions">
                  <el-button
                    type="success"
                    size="small"
                    @click="addToCart(item)"
                    >加入购物车</el-button
                  >
                  <el-button
                    type="primary"
                    class="book-btn"
                    @click="handleBooking(item)"
                    >立即预订</el-button
                  >
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 右侧推荐位 -->
      <div class="right-recommend-section">
        <el-card class="recommend-card">
          <div class="recommend-title">热门推荐</div>
          <div
            class="recommend-item"
            v-for="item in recommendList"
            :key="item.id"
          >
            <img :src="item.image" class="recommend-img" />
            <div class="recommend-info">
              <div class="recommend-name">{{ item.name }}</div>
              <div class="recommend-price">¥{{ item.price }}起</div>
            </div>
          </div>
        </el-card>

        <el-card class="ad-card">
          <img
            src="@/assets/images/welcome/hotel_recommend_5.png"
            class="ad-img"
          />
          <div class="ad-title">春节特惠酒店</div>
          <div class="ad-desc">限时优惠 · 低至3折起</div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { LocationFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

// 路由实例
const router = useRouter();

// 当前活跃的标签页
const activeTab = ref("recommend");

// 模拟酒店数据
const hotelData = {
  recommend: [
    {
      id: 1,
      name: "上海外滩华尔道夫酒店",
      image: require("@/assets/images/welcome/hotel_recommend_1.png"),
      stars: 5,
      description: "位于外滩核心地带，享有黄浦江和陆家嘴天际线的壮丽景色",
      features: ["免费WiFi", "免费早餐", "健身房", "游泳池"],
      location: "外滩·南京东路 距离外滩0.2km",
      price: 2888,
      originalPrice: 3500
    },
    {
      id: 2,
      name: "北京王府井希尔顿酒店",
      image: require("@/assets/images/welcome/hotel_recommend_2.png"),
      stars: 5,
      description: "坐落于王府井商业街核心位置，步行即达故宫和天安门广场",
      features: ["免费WiFi", "商务中心", "接送服务"],
      location: "王府井·天安门 距离故宫0.5km",
      price: 1688,
      originalPrice: 2200
    },
    {
      id: 3,
      name: "广州长隆酒店",
      image: require("@/assets/images/welcome/hotel_recommend_3.png"),
      stars: 4,
      description: "主题度假酒店，毗邻长隆野生动物世界和欢乐世界",
      features: ["免费WiFi", "儿童乐园", "主题房间"],
      location: "番禺长隆 距离长隆欢乐世界0.1km",
      price: 899,
      originalPrice: 1200
    }
  ],
  luxury: [
    {
      id: 4,
      name: "上海宝格丽酒店",
      image: require("@/assets/images/welcome/hotel_recommend_4.png"),
      stars: 5,
      description: "意大利奢华品牌酒店，俯瞰黄浦江，尽享都市奢华体验",
      features: ["管家服务", "米其林餐厅", "SPA", "私人阳台"],
      location: "苏河湾 距离外滩1.5km",
      price: 5888,
      originalPrice: 6800
    },
    {
      id: 5,
      name: "北京瑰丽酒店",
      image: require("@/assets/images/welcome/hotel_recommend_5.png"),
      stars: 5,
      description: "现代奢华酒店，融合中国传统文化与当代设计美学",
      features: ["私人管家", "顶级SPA", "米其林餐厅"],
      location: "朝阳公园 距离三里屯1km",
      price: 4288,
      originalPrice: 5000
    }
  ],
  business: [
    {
      id: 6,
      name: "深圳福田香格里拉酒店",
      image: require("@/assets/images/welcome/hotel_recommend_6.png"),
      stars: 5,
      description: "商务区核心位置，完善的商务设施和会议服务",
      features: ["商务中心", "会议室", "行政酒廊"],
      location: "福田中心区 距离会展中心0.8km",
      price: 1288,
      originalPrice: 1600
    },
    {
      id: 7,
      name: "杭州JW万豪酒店",
      image: require("@/assets/images/welcome/hotel_recommend_7.png"),
      stars: 5,
      description: "位于钱江新城CBD，提供一流的商务服务和设施",
      features: ["商务中心", "健身房", "行政楼层"],
      location: "钱江新城 距离杭州东站15km",
      price: 988,
      originalPrice: 1300
    }
  ],
  resort: [
    {
      id: 8,
      name: "三亚亚龙湾瑞吉度假酒店",
      image: require("@/assets/images/welcome/hotel_recommend_8.png"),
      stars: 5,
      description: "海滨度假酒店，私人海滩，热带园林景观",
      features: ["私人海滩", "水上运动", "儿童俱乐部", "SPA"],
      location: "亚龙湾 距离海滩0km",
      price: 2688,
      originalPrice: 3200
    },
    {
      id: 9,
      name: "丽江古城英迪格酒店",
      image: require("@/assets/images/welcome/hotel_recommend_1.png"),
      stars: 4,
      description: "融合纳西文化的精品度假酒店，坐落古城核心区域",
      features: ["文化体验", "庭院景观", "特色餐厅"],
      location: "丽江古城 距离四方街0.3km",
      price: 1488,
      originalPrice: 1800
    }
  ],
  boutique: [
    {
      id: 10,
      name: "西湖畔精品民宿",
      image: require("@/assets/images/welcome/hotel_recommend_2.png"),
      stars: 4,
      description: "传统江南建筑风格，坐拥西湖美景，体验杭州慢生活",
      features: ["湖景房", "茶艺体验", "传统建筑"],
      location: "西湖风景区 距离西湖0.1km",
      price: 688,
      originalPrice: 900
    },
    {
      id: 11,
      name: "厦门鼓浪屿艺术民宿",
      image: require("@/assets/images/welcome/hotel_recommend_3.png"),
      stars: 4,
      description: "文艺复古风格民宿，感受鼓浪屿的浪漫与艺术气息",
      features: ["艺术装饰", "海景阳台", "文艺氛围"],
      location: "鼓浪屿 距离码头0.2km",
      price: 588,
      originalPrice: 750
    }
  ]
};

// 推荐列表数据
const recommendList = ref([
  {
    id: 1,
    name: "春节特惠酒店",
    image: require("@/assets/images/welcome/hotel_recommend_1.png"),
    price: 299
  },
  {
    id: 2,
    name: "海景度假村",
    image: require("@/assets/images/welcome/hotel_recommend_2.png"),
    price: 1299
  },
  {
    id: 3,
    name: "商务精选",
    image: require("@/assets/images/welcome/hotel_recommend_3.png"),
    price: 599
  }
]);

// 当前显示的酒店列表
const currentHotelList = computed(() => {
  return hotelData[activeTab.value as keyof typeof hotelData] || [];
});

// 标签页切换处理
const handleTabChange = (tabName: string) => {
  activeTab.value = tabName;
  ElMessage.success(`切换到${getTabLabel(tabName)}页面`);
};

// 获取标签页标签
const getTabLabel = (tabName: string) => {
  const labels: Record<string, string> = {
    recommend: "推荐酒店",
    luxury: "豪华酒店",
    business: "商务酒店",
    resort: "度假酒店",
    boutique: "精品民宿"
  };
  return labels[tabName] || tabName;
};

// 处理预订按钮点击
const handleBooking = (hotelItem: any) => {
  // 跳转到支付页面，传递酒店信息
  router.push({
    path: "/user/payment",
    query: {
      spotId: hotelItem.id,
      spotName: hotelItem.name,
      spotPrice: hotelItem.price,
      spotImage: hotelItem.image,
      spotLocation: hotelItem.location,
      spotDescription: hotelItem.description
    }
  });
};

// 添加到购物车
const addToCart = (hotelItem: any) => {
  // 获取现有购物车数据
  const existingCart = JSON.parse(localStorage.getItem("tourismCart") || "[]");

  // 检查是否已存在
  const existingIndex = existingCart.findIndex(
    (item: any) => item.id === hotelItem.id && item.type === "hotel"
  );

  if (existingIndex >= 0) {
    // 如果已存在，增加数量
    existingCart[existingIndex].quantity += 1;
    existingCart[existingIndex].visitors.push({
      name: "",
      phone: "",
      idCard: "",
      visitDate: null
    });
  } else {
    // 如果不存在，添加新项目
    const newItem = {
      id: hotelItem.id,
      type: "hotel",
      name: hotelItem.name,
      price: hotelItem.price,
      image: hotelItem.image,
      location: hotelItem.location,
      description: hotelItem.description,
      quantity: 1,
      visitors: [
        {
          name: "",
          phone: "",
          idCard: "",
          visitDate: null
        }
      ],
      activeCollapse: []
    };
    existingCart.push(newItem);
  }

  // 保存到localStorage
  localStorage.setItem("tourismCart", JSON.stringify(existingCart));
  ElMessage.success("已添加到购物车");
};
</script>

<style scoped lang="scss">
.hotel-portal-container {
  background: #fafbfc;
  min-height: 100vh;
  padding: 20px;
}

.search-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 18px auto;
  max-width: 700px;

  .search-input {
    width: 420px;
    height: 44px;
    font-size: 16px;
    border-radius: 6px 0 0 6px;
  }

  .search-btn {
    height: 44px;
    font-size: 18px;
    border-radius: 0 6px 6px 0;
  }
}

.filter-bar {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  max-width: 1200px;
  margin: 0 auto 24px auto;
  padding: 18px 24px 8px 24px;

  .filter-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    .filter-label {
      color: #888;
      margin-right: 12px;
      font-size: 15px;
      min-width: 56px;
    }

    .filter-btn {
      margin-right: 10px;
      padding: 2px 14px;
      border-radius: 16px;
      background: #f5f7fa;
      color: #222;
      font-size: 15px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #e8f4ff;
      }

      &.active {
        background: #1e80ff;
        color: #fff;
      }
    }
  }
}

.tabs-container {
  max-width: 1200px;
  margin: 0 auto 24px auto;

  .hotel-tabs {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    padding: 0 24px;

    :deep(.el-tabs__header) {
      margin: 0;
    }

    :deep(.el-tabs__nav-wrap) {
      padding: 16px 0;
    }

    :deep(.el-tabs__item) {
      font-size: 16px;
      font-weight: 500;
      padding: 0 24px;

      &.is-active {
        color: #1e80ff;
      }
    }

    :deep(.el-tabs__active-bar) {
      background-color: #1e80ff;
    }
  }
}

.main-content {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  gap: 24px;
}

.hotel-list-section {
  flex: 1 1 0%;
  min-width: 0;
}

.hotel-list-toolbar {
  margin-bottom: 12px;

  .el-checkbox {
    margin-right: 12px;
  }
}

.hotel-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.hotel-card {
  display: flex;
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  .hotel-card-main {
    display: flex;
    align-items: flex-start;
    gap: 18px;
    padding: 18px;
    width: 100%;
  }

  .hotel-img {
    width: 160px;
    height: 120px;
    object-fit: cover;
    border-radius: 8px;
    background: #f5f7fa;
  }

  .hotel-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 8px;

    .hotel-title-row {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .hotel-title {
        font-size: 20px;
        font-weight: bold;
        color: #222;
      }

      .hotel-stars {
        :deep(.el-rate) {
          font-size: 14px;
        }
      }
    }

    .hotel-desc {
      color: #666;
      font-size: 15px;
      line-height: 1.4;
    }

    .hotel-features {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
    }

    .hotel-location {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #888;
      font-size: 14px;
    }
  }

  .hotel-price {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8px;
    min-width: 120px;

    .price-info {
      display: flex;
      align-items: baseline;

      .price-label {
        font-size: 14px;
        color: #888;
        margin-right: 4px;
      }

      .price-value {
        font-size: 24px;
        font-weight: bold;
        color: #ff6b35;
      }

      .price-unit {
        font-size: 14px;
        color: #888;
        margin-left: 2px;
      }
    }

    .price-original {
      font-size: 12px;
      color: #bbb;
      text-decoration: line-through;
    }

    .hotel-actions {
      display: flex;
      gap: 8px;
      flex-direction: column;
    }

    .book-btn {
      border-radius: 20px;
    }
  }
}

.right-recommend-section {
  width: 280px;
  flex-shrink: 0;

  .recommend-card {
    margin-bottom: 24px;
    border-radius: 12px;

    .recommend-title {
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 16px;
      color: #222;
    }

    .recommend-item {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
      padding: 8px;
      border-radius: 8px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: #f5f7fa;
      }

      .recommend-img {
        width: 60px;
        height: 45px;
        object-fit: cover;
        border-radius: 6px;
      }

      .recommend-info {
        flex: 1;

        .recommend-name {
          font-size: 14px;
          color: #222;
          margin-bottom: 4px;
        }

        .recommend-price {
          font-size: 16px;
          font-weight: bold;
          color: #ff6b35;
        }
      }
    }
  }

  .ad-card {
    padding: 0;
    border-radius: 12px;
    overflow: hidden;

    .ad-img {
      width: 100%;
      height: 160px;
      object-fit: cover;
    }

    .ad-title {
      font-size: 16px;
      font-weight: bold;
      margin: 12px 0 4px 0;
      color: #222;
      text-align: center;
    }

    .ad-desc {
      color: #888;
      font-size: 14px;
      text-align: center;
      margin-bottom: 10px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .right-recommend-section {
    width: 100%;
  }

  .hotel-card .hotel-card-main {
    flex-direction: column;

    .hotel-img {
      width: 100%;
      height: 160px;
    }
  }
}
</style>
