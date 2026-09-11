<template>
  <div class="ranking-page">
    <!-- 头部横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">🏆 热门景点排行榜</h1>
        <p class="hero-subtitle">
          发现最受欢迎的旅游目的地，探索热门景点背后的故事
        </p>
        <div class="stats-row">
          <div class="stat-item">
            <div class="stat-number">{{ totalSpots }}</div>
            <div class="stat-label">总景点数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalViews }}</div>
            <div class="stat-label">总浏览量</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalFavors }}</div>
            <div class="stat-label">总收藏量</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 排行榜卡片区域 -->
    <div class="ranking-container">
      <!-- Top 3 特殊展示 -->
      <div class="top-three-section">
        <h2 class="section-title">🥇 前三甲景点</h2>
        <div class="podium">
          <div
            v-for="(spot, index) in topThreeSpots"
            :key="spot.id"
            class="podium-item"
            :class="`rank-${index + 1}`"
          >
            <div class="rank-badge">{{ index + 1 }}</div>
            <div class="spot-image">
              <img :src="getSpotImage(index)" :alt="spot.spotName" />
              <div class="image-overlay">
                <div class="overlay-stats">
                  <div class="stat">
                    <el-icon><View /></el-icon>
                    <span>{{ formatNumber(spot.viewNum) }}</span>
                  </div>
                  <div class="stat">
                    <el-icon><Star /></el-icon>
                    <span>{{ formatNumber(spot.favourNum) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="spot-info">
              <h3 class="spot-name">{{ spot.spotName }}</h3>
              <p class="spot-location">{{ spot.spotLocation || "中国" }}</p>
              <div class="popularity-bar">
                <div
                  class="popularity-fill"
                  :style="{
                    width: getPopularityPercentage(spot.viewNum) + '%'
                  }"
                ></div>
              </div>
              <div class="popularity-text">
                热度: {{ getPopularityPercentage(spot.viewNum) }}%
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表展示区域 -->
      <div class="charts-section">
        <div class="chart-grid">
          <!-- 浏览量趋势图 -->
          <div class="chart-card">
            <h3 class="chart-title">📈 浏览量排行</h3>
            <div ref="barChartRef" class="chart-content"></div>
          </div>

          <!-- 收藏量分布饼图 -->
          <div class="chart-card">
            <h3 class="chart-title">💝 收藏量分布</h3>
            <div ref="pieChartRef" class="chart-content"></div>
          </div>

          <!-- 热度雷达图 -->
          <div class="chart-card">
            <h3 class="chart-title">🎯 热度分析</h3>
            <div ref="radarChartRef" class="chart-content"></div>
          </div>

          <!-- 地区分布图 -->
          <div class="chart-card">
            <h3 class="chart-title">🗺️ 地区分布</h3>
            <div ref="mapChartRef" class="chart-content"></div>
          </div>
        </div>
      </div>

      <!-- 完整排行榜列表 -->
      <div class="full-ranking-section">
        <h2 class="section-title">📊 完整排行榜</h2>
        <div class="ranking-list">
          <div
            v-for="(spot, index) in rankList"
            :key="spot.id"
            class="ranking-item"
            :class="{ 'top-rank': index < 3 }"
          >
            <div class="rank-number">
              <span v-if="index < 3" class="medal">{{
                ["🥇", "🥈", "🥉"][index]
              }}</span>
              <span v-else class="number">{{ index + 1 }}</span>
            </div>
            <div class="spot-avatar">
              <img :src="getSpotImage(index)" :alt="spot.spotName" />
            </div>
            <div class="spot-details">
              <h4 class="name">{{ spot.spotName }}</h4>
              <p class="location">{{ spot.spotLocation || "位置未知" }}</p>
              <div class="tags">
                <el-tag size="small" type="success">{{
                  getCategoryTag(index)
                }}</el-tag>
                <el-tag size="small" type="info">{{
                  getRegionTag(spot.spotLocation)
                }}</el-tag>
              </div>
            </div>
            <div class="metrics">
              <div class="metric">
                <el-icon><View /></el-icon>
                <span class="value">{{ formatNumber(spot.viewNum) }}</span>
                <span class="label">浏览</span>
              </div>
              <div class="metric">
                <el-icon><Star /></el-icon>
                <span class="value">{{ formatNumber(spot.favourNum) }}</span>
                <span class="label">收藏</span>
              </div>
            </div>
            <div class="trend">
              <div class="trend-chart">
                <div
                  v-for="n in 7"
                  :key="n"
                  class="trend-bar"
                  :style="{ height: Math.random() * 100 + '%' }"
                ></div>
              </div>
              <span class="trend-text">{{ getTrendText() }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import * as echarts from "echarts";
import { ElMessage, ElTag } from "element-plus";
import { View, Star } from "@element-plus/icons-vue";
import { getTop10SpotsByViewsUsingGet } from "@/api/spotController";

// 排行榜数据
const rankList = ref([]);

// 图表 DOM 引用
const barChartRef = ref(null);
const pieChartRef = ref(null);
const radarChartRef = ref(null);
const mapChartRef = ref(null);

// 统计数据
const totalSpots = computed(() => rankList.value.length);
const totalViews = computed(() => {
  const total = rankList.value.reduce(
    (sum, item) => sum + (item.viewNum || 0),
    0
  );
  return formatNumber(total);
});
const totalFavors = computed(() => {
  const total = rankList.value.reduce(
    (sum, item) => sum + (item.favourNum || 0),
    0
  );
  return formatNumber(total);
});

// 前三名景点
const topThreeSpots = computed(() => rankList.value.slice(0, 3));

// 获取景点图片
const getSpotImage = (index) => {
  const images = [
    require("@/assets/images/spot/photo1.png"),
    require("@/assets/images/spot/photo2.png"),
    require("@/assets/images/spot/photo3.png"),
    require("@/assets/images/spot/photo4.png"),
    require("@/assets/images/spot/photo5.png"),
    require("@/assets/images/spot/photo6.png"),
    require("@/assets/images/spot/photo7.png"),
    require("@/assets/images/spot/photo8.png"),
    require("@/assets/images/spot/photo9.png"),
    require("@/assets/images/spot/photo10.png"),
    require("@/assets/images/spot/photo11.png"),
    require("@/assets/images/spot/photo12.png")
  ];
  return images[index % images.length];
};

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + "万";
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + "k";
  }
  return num.toString();
};

// 获取热度百分比
const getPopularityPercentage = (viewNum) => {
  if (rankList.value.length === 0) return 0;
  const maxViews = Math.max(...rankList.value.map((item) => item.viewNum || 0));
  return Math.round((viewNum / maxViews) * 100);
};

// 获取分类标签
const getCategoryTag = (index) => {
  const categories = [
    "自然风光",
    "历史文化",
    "现代都市",
    "古镇水乡",
    "山水名胜",
    "主题乐园",
    "宗教圣地",
    "海滨度假",
    "草原风情",
    "沙漠奇观",
    "温泉养生",
    "民俗风情"
  ];
  return categories[index % categories.length];
};

// 获取地区标签
const getRegionTag = (location) => {
  if (!location) return "未知地区";
  if (location.includes("北京")) return "华北地区";
  if (
    location.includes("上海") ||
    location.includes("江苏") ||
    location.includes("浙江")
  )
    return "华东地区";
  if (location.includes("广东") || location.includes("深圳")) return "华南地区";
  if (location.includes("四川") || location.includes("重庆")) return "西南地区";
  if (location.includes("陕西") || location.includes("西安")) return "西北地区";
  if (location.includes("湖北") || location.includes("湖南")) return "华中地区";
  return "其他地区";
};

// 获取趋势文本
const getTrendText = () => {
  const trends = ["📈 上升", "📉 下降", "➡️ 平稳", "🔥 火爆"];
  return trends[Math.floor(Math.random() * trends.length)];
};

// 获取排行榜数据
const fetchRankList = async () => {
  try {
    const res = await getTop10SpotsByViewsUsingGet();

    // 处理API数据，添加模拟数据以丰富展示
    const apiData = res.data || [];
    const enrichedData = apiData.map((item, index) => ({
      ...item,
      id: item.id || index + 1,
      favourNum: item.favourNum || Math.floor(Math.random() * 5000) + 500,
      spotLocation: item.spotLocation || getRandomLocation()
    }));

    // 如果API数据不足，添加一些示例数据
    if (enrichedData.length < 10) {
      const sampleData = [
        {
          id: 101,
          spotName: "故宫博物院",
          spotLocation: "北京市东城区",
          viewNum: 8500,
          favourNum: 4200
        },
        {
          id: 102,
          spotName: "张家界国家森林公园",
          spotLocation: "湖南省张家界市",
          viewNum: 7800,
          favourNum: 3800
        },
        {
          id: 103,
          spotName: "九寨沟",
          spotLocation: "四川省阿坝州",
          viewNum: 7200,
          favourNum: 3600
        },
        {
          id: 104,
          spotName: "桂林漓江",
          spotLocation: "广西省桂林市",
          viewNum: 6900,
          favourNum: 3400
        },
        {
          id: 105,
          spotName: "黄山",
          spotLocation: "安徽省黄山市",
          viewNum: 6500,
          favourNum: 3200
        },
        {
          id: 106,
          spotName: "泰山",
          spotLocation: "山东省泰安市",
          viewNum: 6200,
          favourNum: 3000
        },
        {
          id: 107,
          spotName: "华山",
          spotLocation: "陕西省渭南市",
          viewNum: 5800,
          favourNum: 2800
        },
        {
          id: 108,
          spotName: "峨眉山",
          spotLocation: "四川省乐山市",
          viewNum: 5500,
          favourNum: 2600
        },
        {
          id: 109,
          spotName: "武夷山",
          spotLocation: "福建省南平市",
          viewNum: 5200,
          favourNum: 2400
        },
        {
          id: 110,
          spotName: "天山天池",
          spotLocation: "新疆维吾尔自治区",
          viewNum: 4900,
          favourNum: 2200
        }
      ];

      // 补充数据到10个
      while (enrichedData.length < 10 && sampleData.length > 0) {
        enrichedData.push(sampleData.shift());
      }
    }

    rankList.value = enrichedData.slice(0, 10);

    // 渲染所有图表
    setTimeout(() => {
      renderBarChart();
      renderPieChart();
      renderRadarChart();
      renderMapChart();
    }, 100);
  } catch (error) {
    console.error("获取排行榜数据失败:", error);
    ElMessage.error("获取排行榜数据失败");

    // 使用默认数据
    loadDefaultData();
  }
};

// 获取随机位置
const getRandomLocation = () => {
  const locations = [
    "北京市",
    "上海市",
    "广州市",
    "深圳市",
    "杭州市",
    "南京市",
    "苏州市",
    "成都市",
    "西安市",
    "武汉市"
  ];
  return locations[Math.floor(Math.random() * locations.length)];
};

// 加载默认数据
const loadDefaultData = () => {
  rankList.value = [
    {
      id: 1,
      spotName: "故宫博物院",
      spotLocation: "北京市东城区",
      viewNum: 8500,
      favourNum: 4200
    },
    {
      id: 2,
      spotName: "张家界国家森林公园",
      spotLocation: "湖南省张家界市",
      viewNum: 7800,
      favourNum: 3800
    },
    {
      id: 3,
      spotName: "九寨沟",
      spotLocation: "四川省阿坝州",
      viewNum: 7200,
      favourNum: 3600
    },
    {
      id: 4,
      spotName: "桂林漓江",
      spotLocation: "广西省桂林市",
      viewNum: 6900,
      favourNum: 3400
    },
    {
      id: 5,
      spotName: "黄山",
      spotLocation: "安徽省黄山市",
      viewNum: 6500,
      favourNum: 3200
    },
    {
      id: 6,
      spotName: "泰山",
      spotLocation: "山东省泰安市",
      viewNum: 6200,
      favourNum: 3000
    },
    {
      id: 7,
      spotName: "华山",
      spotLocation: "陕西省渭南市",
      viewNum: 5800,
      favourNum: 2800
    },
    {
      id: 8,
      spotName: "峨眉山",
      spotLocation: "四川省乐山市",
      viewNum: 5500,
      favourNum: 2600
    },
    {
      id: 9,
      spotName: "武夷山",
      spotLocation: "福建省南平市",
      viewNum: 5200,
      favourNum: 2400
    },
    {
      id: 10,
      spotName: "天山天池",
      spotLocation: "新疆维吾尔自治区",
      viewNum: 4900,
      favourNum: 2200
    }
  ];

  setTimeout(() => {
    renderBarChart();
    renderPieChart();
    renderRadarChart();
    renderMapChart();
  }, 100);
};

// 渲染柱状图
const renderBarChart = () => {
  if (!barChartRef.value) return;

  const chart = echarts.init(barChartRef.value);
  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" }
    },
    grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
    xAxis: {
      type: "category",
      data: rankList.value.map((item) =>
        item.spotName.length > 4
          ? item.spotName.slice(0, 4) + "..."
          : item.spotName
      ),
      axisLabel: { rotate: 45 }
    },
    yAxis: { type: "value" },
    series: [
      {
        type: "bar",
        data: rankList.value.map((item) => item.viewNum),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#667eea" },
            { offset: 1, color: "#764ba2" }
          ])
        },
        emphasis: { itemStyle: { color: "#5a67d8" } }
      }
    ]
  };
  chart.setOption(option);
};

// 渲染饼图
const renderPieChart = () => {
  if (!pieChartRef.value) return;

  const chart = echarts.init(pieChartRef.value);
  const option = {
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        radius: ["40%", "70%"],
        center: ["50%", "50%"],
        data: rankList.value.slice(0, 6).map((item) => ({
          name: item.spotName,
          value: item.favourNum
        })),
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 2
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        }
      }
    ]
  };
  chart.setOption(option);
};

// 渲染雷达图
const renderRadarChart = () => {
  if (!radarChartRef.value) return;

  const chart = echarts.init(radarChartRef.value);
  const option = {
    tooltip: {},
    radar: {
      indicator: [
        {
          name: "浏览量",
          max: Math.max(...rankList.value.map((item) => item.viewNum))
        },
        {
          name: "收藏量",
          max: Math.max(...rankList.value.map((item) => item.favourNum))
        },
        { name: "热度指数", max: 100 },
        { name: "用户评分", max: 10 },
        { name: "推荐指数", max: 10 }
      ]
    },
    series: [
      {
        type: "radar",
        data: rankList.value.slice(0, 3).map((item, index) => ({
          value: [
            item.viewNum,
            item.favourNum,
            getPopularityPercentage(item.viewNum),
            Math.random() * 10,
            Math.random() * 10
          ],
          name: item.spotName,
          itemStyle: { color: ["#667eea", "#764ba2", "#f093fb"][index] }
        }))
      }
    ]
  };
  chart.setOption(option);
};

// 渲染地图分布
const renderMapChart = () => {
  if (!mapChartRef.value) return;

  const chart = echarts.init(mapChartRef.value);

  // 地区统计
  const regionData = {};
  rankList.value.forEach((item) => {
    const region = getRegionTag(item.spotLocation);
    regionData[region] = (regionData[region] || 0) + 1;
  });

  const option = {
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        radius: "60%",
        data: Object.entries(regionData).map(([name, value]) => ({
          name,
          value
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        }
      }
    ]
  };
  chart.setOption(option);
};

// 页面加载时获取数据
onMounted(() => {
  fetchRankList();
});
</script>

<style scoped>
.ranking-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.ranking-page::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.1)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>')
    repeat;
  opacity: 0.3;
}

/* 头部横幅 */
.hero-banner {
  position: relative;
  padding: 80px 20px 60px;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 20px;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  background: linear-gradient(45deg, #fff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.3rem;
  margin-bottom: 40px;
  opacity: 0.9;
  line-height: 1.6;
}

.stats-row {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.stat-label {
  font-size: 1rem;
  opacity: 0.8;
}

/* 主要内容区域 */
.ranking-container {
  position: relative;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  text-align: center;
  margin-bottom: 40px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* Top 3 展示区 */
.top-three-section {
  margin-bottom: 60px;
}

.podium {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  max-width: 1000px;
  margin: 0 auto;
}

.podium-item {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  text-align: center;
  position: relative;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.podium-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.2);
}

.podium-item.rank-1 {
  border: 3px solid #ffd700;
  transform: scale(1.05);
}

.podium-item.rank-2 {
  border: 3px solid #c0c0c0;
}

.podium-item.rank-3 {
  border: 3px solid #cd7f32;
}

.rank-badge {
  position: absolute;
  top: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.2rem;
}

.spot-image {
  position: relative;
  width: 200px;
  height: 150px;
  margin: 20px auto;
  border-radius: 15px;
  overflow: hidden;
}

.spot-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.podium-item:hover .spot-image img {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.7));
  display: flex;
  align-items: flex-end;
  padding: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.podium-item:hover .image-overlay {
  opacity: 1;
}

.overlay-stats {
  display: flex;
  gap: 15px;
  color: white;
}

.overlay-stats .stat {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
}

.spot-info {
  margin-top: 20px;
}

.spot-name {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.spot-location {
  color: #666;
  margin-bottom: 15px;
}

.popularity-bar {
  width: 100%;
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.popularity-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.8s ease;
}

.popularity-text {
  font-size: 0.9rem;
  color: #666;
  font-weight: 500;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 60px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 30px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 25px;
  backdrop-filter: blur(10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-5px);
}

.chart-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.chart-content {
  height: 300px;
  width: 100%;
}

/* 完整排行榜 */
.full-ranking-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.full-ranking-section .section-title {
  color: #333;
  margin-bottom: 30px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ranking-item {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 15px;
  padding: 20px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.ranking-item:hover {
  background: #fff;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transform: translateX(5px);
}

.ranking-item.top-rank {
  background: linear-gradient(135deg, #fff5e6, #fff);
  border-color: #ffd700;
}

.rank-number {
  width: 60px;
  text-align: center;
  margin-right: 20px;
}

.medal {
  font-size: 2rem;
}

.number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
}

.spot-avatar {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  margin-right: 20px;
  flex-shrink: 0;
}

.spot-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spot-details {
  flex: 1;
  margin-right: 20px;
}

.spot-details .name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.spot-details .location {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.tags {
  display: flex;
  gap: 8px;
}

.metrics {
  display: flex;
  gap: 30px;
  margin-right: 20px;
}

.metric {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.metric .value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 5px 0;
}

.metric .label {
  font-size: 0.8rem;
  color: #666;
}

.trend {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80px;
}

.trend-chart {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 30px;
  margin-bottom: 5px;
}

.trend-bar {
  width: 4px;
  background: linear-gradient(to top, #667eea, #764ba2);
  border-radius: 2px;
  min-height: 5px;
}

.trend-text {
  font-size: 0.8rem;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1.1rem;
  }

  .stats-row {
    flex-direction: column;
    gap: 30px;
  }

  .podium {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .chart-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .ranking-item {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .metrics {
    justify-content: center;
  }

  .ranking-container {
    padding: 0 15px 40px;
  }

  .full-ranking-section {
    padding: 25px 20px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }

  .spot-image {
    width: 150px;
    height: 120px;
  }

  .chart-content {
    height: 250px;
  }
}
</style>
