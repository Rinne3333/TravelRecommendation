<template>
  <div class="spot-portal-container">
    <!-- 顶部导航栏 -->

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input class="search-input" placeholder="请输入景点名称、城市" />
      <el-button type="primary" class="search-btn">搜索景点</el-button>
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
        <span class="filter-label">景点类型</span>
        <span class="filter-btn active">全部类型</span>
        <span class="filter-btn">自然风光</span>
        <span class="filter-btn">名胜古迹</span>
        <span class="filter-btn">主题乐园</span>
        <span class="filter-btn">博物馆</span>
        <span class="filter-btn">动物园</span>
        <span class="filter-btn">海滨沙滩</span>
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
        class="spot-tabs"
      >
        <el-tab-pane label="热门推荐" name="recommend"></el-tab-pane>
        <el-tab-pane label="自然风光" name="nature"></el-tab-pane>
        <el-tab-pane label="名胜古迹" name="historic"></el-tab-pane>
        <el-tab-pane label="主题乐园" name="theme"></el-tab-pane>
        <el-tab-pane label="文化博物" name="culture"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 主体内容区 -->
    <div class="main-content">
      <!-- 左侧景点列表 -->
      <div class="spot-list-section">
        <div class="spot-list-toolbar">
          <el-checkbox>可订今日</el-checkbox>
          <el-checkbox>免费景点</el-checkbox>
          <el-checkbox>5A景区</el-checkbox>
          <el-checkbox>亲子推荐</el-checkbox>
          <el-checkbox>网红打卡</el-checkbox>
          <el-checkbox>夜游开放</el-checkbox>
        </div>

        <div class="spot-list">
          <el-card
            v-for="item in currentSpotList"
            :key="item.id"
            class="spot-card"
            @click="goToSpotDetail(item)"
          >
            <div class="spot-card-main">
              <img :src="item.image" class="spot-img" />
              <div class="spot-info">
                <div class="spot-title-row">
                  <span class="spot-title">{{ item.name }}</span>
                  <div class="spot-rating">
                    <el-rate
                      :model-value="item.rating"
                      disabled
                      show-score
                      text-color="#ff9900"
                      score-template="{value}分"
                    />
                  </div>
                </div>
                <div class="spot-desc">
                  {{ item.description }}
                </div>
                <div class="spot-features">
                  <el-tag
                    v-for="feature in item.features"
                    :key="feature"
                    type="success"
                    size="small"
                  >
                    {{ feature }}
                  </el-tag>
                </div>
                <div class="spot-location">
                  <el-icon><LocationFilled /></el-icon>
                  {{ item.location }}
                </div>
              </div>
              <div class="spot-price">
                <div class="price-info">
                  <span class="price-label">门票</span>
                  <span class="price-value">¥{{ item.price }}</span>
                  <span class="price-unit">起</span>
                </div>
                <div class="price-original" v-if="item.originalPrice">
                  原价：¥{{ item.originalPrice }}
                </div>
                <div class="spot-actions">
                  <el-button
                    type="success"
                    size="small"
                    @click.stop="addToCart(item)"
                    >加入购物车</el-button
                  >
                  <el-button
                    type="primary"
                    class="book-btn"
                    @click.stop="handleBooking(item)"
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
          <div class="recommend-title">热门景点</div>
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
          <img src="@/assets/images/spot/photo12.png" class="ad-img" />
          <div class="ad-title">春节特惠景点</div>
          <div class="ad-desc">限时优惠 · 低至5折起</div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { LocationFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

// 路由实例
const router = useRouter();

// 当前活跃的标签页
const activeTab = ref("recommend");

// 模拟景点数据
const spotData = {
  recommend: [
    {
      id: 1,
      name: "故宫博物院",
      image: require("@/assets/images/spot/photo1.png"),
      rating: 4.8,
      description: "明清两朝的皇家宫殿，世界文化遗产，中华文明的瑰宝",
      features: ["5A景区", "世界遗产", "免费讲解", "网红打卡"],
      location: "北京市东城区 距离天安门0.5km",
      price: 60,
      originalPrice: 80
    },
    {
      id: 2,
      name: "外滩",
      image: require("@/assets/images/spot/photo2.png"),
      rating: 4.7,
      description: "上海最著名的滨江景观带，欣赏黄浦江两岸风光的最佳地点",
      features: ["免费景点", "夜景绝佳", "历史建筑", "江景步道"],
      location: "上海市黄浦区 距离南京东路0.3km",
      price: 0,
      originalPrice: null
    },
    {
      id: 3,
      name: "长城·八达岭",
      image: require("@/assets/images/spot/photo3.png"),
      rating: 4.9,
      description: "万里长城的精华段落，世界新七大奇迹之一",
      features: ["5A景区", "世界遗产", "缆车服务", "登高望远"],
      location: "北京市延庆区 距离市区60km",
      price: 45,
      originalPrice: 60
    }
  ],
  nature: [
    {
      id: 4,
      name: "张家界国家森林公园",
      image: require("@/assets/images/spot/photo4.png"),
      rating: 4.8,
      description: "世界自然遗产，奇峰异石，云雾缭绕的仙境",
      features: ["5A景区", "世界遗产", "玻璃桥", "索道观光"],
      location: "湖南省张家界市 武陵源风景区",
      price: 248,
      originalPrice: 320
    },
    {
      id: 5,
      name: "九寨沟",
      image: require("@/assets/images/spot/photo5.png"),
      rating: 4.9,
      description: "人间仙境，五彩斑斓的湖泊和瀑布群",
      features: ["5A景区", "世界遗产", "观光车", "摄影天堂"],
      location: "四川省阿坝州 九寨沟县",
      price: 169,
      originalPrice: 220
    },
    {
      id: 6,
      name: "黄山风景区",
      image: require("@/assets/images/spot/photo6.png"),
      rating: 4.8,
      description: "奇松、怪石、云海、温泉，中国山水画的典型代表",
      features: ["5A景区", "世界遗产", "索道缆车", "日出云海"],
      location: "安徽省黄山市 黄山区",
      price: 190,
      originalPrice: 230
    }
  ],
  historic: [
    {
      id: 7,
      name: "兵马俑博物馆",
      image: require("@/assets/images/spot/photo7.png"),
      rating: 4.8,
      description: "世界第八大奇迹，秦始皇陵的守护军团",
      features: ["5A景区", "世界遗产", "专业讲解", "历史震撼"],
      location: "陕西省西安市 临潼区",
      price: 120,
      originalPrice: 150
    },
    {
      id: 8,
      name: "天坛公园",
      image: require("@/assets/images/spot/photo8.png"),
      rating: 4.7,
      description: "明清皇帝祭天的圣地，古代建筑艺术的杰作",
      features: ["5A景区", "世界遗产", "古建筑群", "晨练圣地"],
      location: "北京市东城区 天坛路",
      price: 15,
      originalPrice: 20
    }
  ],
  theme: [
    {
      id: 9,
      name: "上海迪士尼乐园",
      image: require("@/assets/images/spot/photo9.png"),
      rating: 4.6,
      description: "神奇王国，梦幻城堡，适合全家游玩的主题乐园",
      features: ["亲子推荐", "刺激项目", "演出表演", "主题餐厅"],
      location: "上海市浦东新区 川沙新镇",
      price: 399,
      originalPrice: 499
    },
    {
      id: 10,
      name: "长隆欢乐世界",
      image: require("@/assets/images/spot/photo10.png"),
      rating: 4.5,
      description: "亚洲顶级主题乐园，刺激项目和精彩演出",
      features: ["亲子推荐", "过山车", "水上乐园", "动物表演"],
      location: "广东省广州市 番禺区",
      price: 280,
      originalPrice: 350
    }
  ],
  culture: [
    {
      id: 11,
      name: "中国国家博物馆",
      image: require("@/assets/images/spot/photo11.png"),
      rating: 4.7,
      description: "中华文明的宝库，收藏着丰富的历史文物",
      features: ["免费景点", "专业讲解", "文物珍藏", "教育意义"],
      location: "北京市东城区 天安门广场东侧",
      price: 0,
      originalPrice: null
    },
    {
      id: 12,
      name: "苏州博物馆",
      image: require("@/assets/images/spot/photo12.png"),
      rating: 4.6,
      description: "贝聿铭设计的现代博物馆，江南文化的精华展示",
      features: ["免费景点", "建筑艺术", "江南文化", "园林美学"],
      location: "江苏省苏州市 姑苏区",
      price: 0,
      originalPrice: null
    }
  ]
};

// 推荐列表数据
const recommendList = ref([
  {
    id: 1,
    name: "春节特惠景点",
    image: require("@/assets/images/spot/photo9.png"),
    price: 99
  },
  {
    id: 2,
    name: "网红打卡地",
    image: require("@/assets/images/spot/photo6.png"),
    price: 168
  },
  {
    id: 3,
    name: "亲子游乐园",
    image: require("@/assets/images/spot/photo4.png"),
    price: 299
  }
]);

// 当前显示的景点列表
const currentSpotList = computed(() => {
  return spotData[activeTab.value as keyof typeof spotData] || [];
});

// 标签页切换处理
const handleTabChange = (tabName: string) => {
  activeTab.value = tabName;
  ElMessage.success(`切换到${getTabLabel(tabName)}页面`);
};

// 获取标签页标签
const getTabLabel = (tabName: string) => {
  const labels: Record<string, string> = {
    recommend: "热门推荐",
    nature: "自然风光",
    historic: "名胜古迹",
    theme: "主题乐园",
    culture: "文化博物"
  };
  return labels[tabName] || tabName;
};

// 跳转到景点详情页面
const goToSpotDetail = (spotItem: any) => {
  console.log("点击景点:", spotItem);

  // 将景点数据存储到localStorage，供详情页使用
  localStorage.setItem("currentSpotDetail", JSON.stringify(spotItem));
  console.log("数据已存储到localStorage");

  // 检查路由是否存在
  const routeExists = router.hasRoute("spotDetail");
  console.log("景点详情路由是否存在:", routeExists);

  if (routeExists) {
    // 使用路由名称跳转
    router
      .push({
        name: "spotDetail",
        params: {
          id: spotItem.id
        }
      })
      .catch((err) => {
        console.error("路由跳转失败:", err);
      });
  } else {
    // 如果路由不存在，直接跳转到URL
    console.warn("路由不存在，使用URL跳转");
    window.location.href = `#/user/spot/detail/${spotItem.id}`;
  }
};

// 处理预订按钮点击
const handleBooking = (spotItem: any) => {
  // 跳转到支付页面，传递景点信息
  router.push({
    path: "/user/payment",
    query: {
      spotId: spotItem.id,
      spotName: spotItem.name,
      spotPrice: spotItem.price,
      spotImage: spotItem.image,
      spotLocation: spotItem.location,
      spotDescription: spotItem.description
    }
  });
};

// 添加到购物车
const addToCart = (spotItem: any) => {
  // 获取现有购物车数据
  const existingCart = JSON.parse(localStorage.getItem("tourismCart") || "[]");

  // 检查是否已存在
  const existingIndex = existingCart.findIndex(
    (item: any) => item.id === spotItem.id && item.type === "spot"
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
      id: spotItem.id,
      type: "spot",
      name: spotItem.name,
      price: spotItem.price,
      image: spotItem.image,
      location: spotItem.location,
      description: spotItem.description,
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

// 初始化
onMounted(() => {
  ElMessage.success("欢迎来到景点预订平台");
});
</script>

<style scoped lang="scss">
.spot-portal-container {
  background: #fafbfc;
  min-height: 100vh;
}

.portal-header {
  display: flex;
  align-items: center;
  padding: 32px 0 16px 0;
  max-width: 1200px;
  margin: 0 auto;

  .logo {
    font-size: 28px;
    font-weight: bold;
    margin-right: 32px;
    color: #1e80ff;

    .logo-en {
      color: #bfc6d1;
      font-size: 22px;
      margin-left: 4px;
    }
  }

  .main-nav {
    display: flex;
    gap: 24px;

    .nav-item {
      font-size: 18px;
      color: #222;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: #1e80ff;
      }

      &.active {
        color: #1e80ff;
        font-weight: bold;
      }
    }
  }
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

  .spot-tabs {
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

.spot-list-section {
  flex: 1 1 0%;
  min-width: 0;
}

.spot-list-toolbar {
  margin-bottom: 12px;

  .el-checkbox {
    margin-right: 12px;
  }
}

.spot-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.spot-card {
  display: flex;
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }

  .spot-card-main {
    display: flex;
    align-items: flex-start;
    gap: 18px;
    padding: 18px;
    width: 100%;
  }

  .spot-img {
    width: 160px;
    height: 120px;
    object-fit: cover;
    border-radius: 8px;
    background: #f5f7fa;
  }

  .spot-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 8px;

    .spot-title-row {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .spot-title {
        font-size: 20px;
        font-weight: bold;
        color: #222;
      }

      .spot-rating {
        :deep(.el-rate) {
          font-size: 14px;
        }
      }
    }

    .spot-desc {
      color: #666;
      font-size: 15px;
      line-height: 1.4;
    }

    .spot-features {
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
    }

    .spot-location {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #888;
      font-size: 14px;
    }
  }

  .spot-price {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: space-between;
    gap: 12px;
    min-width: 140px;
    padding: 8px 0;
    text-align: right;

    .price-info {
      display: flex;
      align-items: baseline;
      justify-content: flex-end;
      width: 100%;

      .price-label {
        font-size: 14px;
        color: #888;
        margin-right: 6px;
      }

      .price-value {
        font-size: 26px;
        font-weight: bold;
        color: #ff6b35;
        line-height: 1;
      }

      .price-unit {
        font-size: 14px;
        color: #888;
        margin-left: 3px;
      }
    }

    .price-original {
      font-size: 12px;
      color: #bbb;
      text-decoration: line-through;
      width: 100%;
      text-align: right;
      margin-top: -4px;
    }

    .spot-actions {
      display: flex;
      gap: 8px;
      flex-direction: column;
      width: 100%;
      align-items: flex-end;

      .el-button {
        width: 100px;
        text-align: center;
      }
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

  .spot-card .spot-card-main {
    flex-direction: column;

    .spot-img {
      width: 100%;
      height: 160px;
    }
  }
}
</style>
