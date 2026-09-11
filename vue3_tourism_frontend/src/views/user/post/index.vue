<template>
  <div class="travel-guide-container">
    <!-- 页面头部 -->
    <div class="guide-header">
      <div class="header-content">
        <h1 class="page-title">旅游攻略</h1>
        <p class="page-subtitle">TRAVEL GUIDES</p>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ totalGuides }}</span>
            <span class="stat-label">精选攻略</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ totalViews }}</span>
            <span class="stat-label">总浏览量</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ totalAuthors }}</span>
            <span class="stat-label">旅行达人</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <div class="search-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索攻略标题、目的地..."
          size="large"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prepend>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="filter-tabs">
        <span
          v-for="tab in filterTabs"
          :key="tab.key"
          :class="['filter-tab', { active: activeFilter === tab.key }]"
          @click="handleFilterChange(tab.key)"
        >
          <el-icon><component :is="tab.icon" /></el-icon>
          {{ tab.label }}
        </span>
      </div>

      <div class="action-buttons">
        <el-button
          type="primary"
          size="large"
          @click="showAddPost()"
          :icon="Edit"
          v-if="!addPost"
        >
          分享攻略
        </el-button>
        <el-button
          type="success"
          size="large"
          @click="addPost = false"
          :icon="ArrowLeft"
          v-if="addPost"
        >
          返回攻略列表
        </el-button>
      </div>
    </div>

    <!-- 发布攻略组件 -->
    <AddPost v-if="addPost" @close="addPost = false"></AddPost>

    <!-- 攻略列表 -->
    <div class="guide-list" v-if="!addPost">
      <div
        v-for="guide in filteredGuides"
        :key="guide.id"
        class="guide-card"
        @click="goToPostDetail(guide.id)"
      >
        <!-- 攻略封面图 -->
        <div class="guide-cover">
          <img :src="guide.coverImage" :alt="guide.title" />
          <div class="guide-overlay">
            <div class="guide-category">{{ guide.category }}</div>
            <div class="guide-duration">{{ guide.duration }}</div>
          </div>
        </div>

        <!-- 攻略内容 -->
        <div class="guide-content">
          <!-- 用户信息 -->
          <div class="author-info">
            <el-avatar :src="guide.user?.userAvatar" size="small" />
            <div class="author-details">
              <span class="author-name">{{ guide.user?.userName }}</span>
              <span class="publish-time">{{
                formatDate(guide.createTime)
              }}</span>
            </div>
            <div class="author-level">
              <el-tag
                size="small"
                :type="getAuthorLevelType(guide.authorLevel)"
              >
                {{ guide.authorLevel }}
              </el-tag>
            </div>
          </div>

          <!-- 攻略标题 -->
          <h3 class="guide-title">{{ guide.title }}</h3>

          <!-- 攻略摘要 -->
          <p class="guide-summary">{{ guide.summary }}</p>

          <!-- 攻略亮点 -->
          <div class="guide-highlights">
            <span
              v-for="highlight in guide.highlights"
              :key="highlight"
              class="highlight-tag"
            >
              {{ highlight }}
            </span>
          </div>

          <!-- 攻略标签 -->
          <div class="guide-tags">
            <el-tag
              v-for="tag in guide.tagList"
              :key="tag"
              size="small"
              class="tag"
            >
              {{ tag }}
            </el-tag>
          </div>

          <!-- 攻略统计 -->
          <div class="guide-stats">
            <div class="stat-group">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ guide.viewCount }}
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ guide.favourNum }}
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ guide.commentCount }}
              </span>
            </div>
            <div class="guide-price">
              <span class="price-label">人均消费</span>
              <span class="price-value">¥{{ guide.avgCost }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="!addPost && filteredGuides.length > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[6, 12, 18, 24]"
        :current-page="paginationConfig.current"
        :total="paginationConfig.total"
        :page-size="paginationConfig.pageSize"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 空状态 -->
    <div v-if="!addPost && filteredGuides.length === 0" class="empty-state">
      <el-empty description="暂无攻略内容">
        <el-button type="primary" @click="showAddPost()"
          >发布第一篇攻略</el-button
        >
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  onMounted,
  onUnmounted,
  ref,
  computed,
  defineAsyncComponent
} from "vue";
import {
  Search,
  Star,
  ArrowLeft,
  Edit,
  View,
  ChatDotRound
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { listPostVoByPageUsingPost } from "@/api/postController";
import { useRouter } from "vue-router";
import eventBus from "@/utils/eventBus";

// 导入AddPost组件
const AddPost = defineAsyncComponent(
  () => import("@/components/AddPost/index.vue")
);

// 搜索文本
const searchText = ref("");
const activeFilter = ref("all");

// 攻略列表
const postList = ref<any[]>([]);
const addPost = ref(false);
const router = useRouter();

// 分页配置
const paginationConfig = ref({
  current: 1,
  pageSize: 12,
  total: 0
});

// 筛选标签
const filterTabs = ref([
  { key: "all", label: "全部攻略", icon: "Trophy" },
  { key: "popular", label: "热门推荐", icon: "Trophy" },
  { key: "recent", label: "最新发布", icon: "Calendar" },
  { key: "destination", label: "目的地", icon: "LocationFilled" }
]);

// 统计数据
const totalGuides = ref(156);
const totalViews = ref("2.8万");
const totalAuthors = ref(89);

// 模拟攻略数据
const mockGuides = ref([
  {
    id: 1,
    title: "三亚5天4晚海岛度假完美攻略",
    summary:
      "从亚龙湾到天涯海角，带你玩转三亚最美海滩，享受阳光沙滩的惬意时光。包含详细行程安排、美食推荐、住宿攻略。",
    coverImage: require("@/assets/images/spot/photo1.png"),
    category: "海岛度假",
    duration: "5天4晚",
    avgCost: 3500,
    viewCount: 2856,
    favourNum: 156,
    commentCount: 42,
    authorLevel: "旅行达人",
    highlights: ["亚龙湾", "天涯海角", "南山寺", "蜈支洲岛"],
    tagList: ["三亚", "海岛", "度假", "亲子"],
    user: {
      userName: "海岛小王子",
      userAvatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
    },
    createTime: new Date(Date.now() - 86400000 * 2).toISOString()
  },
  {
    id: 2,
    title: "北京故宫-长城经典3日游攻略",
    summary:
      "深度游览北京必去景点，故宫博物院、八达岭长城、天坛公园，感受千年古都的历史文化底蕴。",
    coverImage: require("@/assets/images/spot/photo2.png"),
    category: "文化古迹",
    duration: "3天2晚",
    avgCost: 1800,
    viewCount: 3247,
    favourNum: 203,
    commentCount: 67,
    authorLevel: "资深驴友",
    highlights: ["故宫博物院", "八达岭长城", "天坛公园", "王府井"],
    tagList: ["北京", "文化", "历史", "经典"],
    user: {
      userName: "古都行者",
      userAvatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
    },
    createTime: new Date(Date.now() - 86400000 * 5).toISOString()
  },
  {
    id: 3,
    title: "张家界天门山玻璃栈道惊险之旅",
    summary:
      "挑战世界最长玻璃栈道，体验云端漫步的刺激感受。详细介绍张家界国家森林公园、天门山、黄龙洞等景点。",
    coverImage: require("@/assets/images/spot/photo3.png"),
    category: "自然风光",
    duration: "4天3晚",
    avgCost: 2200,
    viewCount: 1892,
    favourNum: 134,
    commentCount: 38,
    authorLevel: "探险专家",
    highlights: ["玻璃栈道", "天门洞", "金鞭溪", "袁家界"],
    tagList: ["张家界", "自然", "探险", "刺激"],
    user: {
      userName: "山峰征服者",
      userAvatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
    },
    createTime: new Date(Date.now() - 86400000 * 7).toISOString()
  },
  {
    id: 4,
    title: "西安兵马俑-华清宫历史文化深度游",
    summary:
      "探寻千年古都西安的历史密码，兵马俑震撼人心，华清宫温泉养生，回民街美食天堂。",
    coverImage: require("@/assets/images/spot/photo4.png"),
    category: "文化古迹",
    duration: "4天3晚",
    avgCost: 2000,
    viewCount: 2134,
    favourNum: 167,
    commentCount: 51,
    authorLevel: "文化学者",
    highlights: ["兵马俑", "华清宫", "大雁塔", "回民街"],
    tagList: ["西安", "历史", "文化", "美食"],
    user: {
      userName: "文史专家",
      userAvatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
    },
    createTime: new Date(Date.now() - 86400000 * 10).toISOString()
  },
  {
    id: 5,
    title: "桂林漓江阳朔山水甲天下",
    summary:
      "乘船游漓江，体验'舟行碧波上，人在画中游'的绝美意境。阳朔西街夜生活，遇龙河竹筏漂流。",
    coverImage: require("@/assets/images/spot/photo5.png"),
    category: "自然风光",
    duration: "5天4晚",
    avgCost: 2800,
    viewCount: 2567,
    favourNum: 189,
    commentCount: 73,
    authorLevel: "摄影达人",
    highlights: ["漓江游船", "象鼻山", "阳朔西街", "遇龙河"],
    tagList: ["桂林", "山水", "摄影", "漂流"],
    user: {
      userName: "山水画师",
      userAvatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
    },
    createTime: new Date(Date.now() - 86400000 * 12).toISOString()
  },
  {
    id: 6,
    title: "丽江古城-玉龙雪山浪漫之旅",
    summary:
      "漫步丽江古城石板路，感受纳西文化魅力。玉龙雪山索道观光，泸沽湖女儿国风情。",
    coverImage: require("@/assets/images/spot/photo6.png"),
    category: "文化风情",
    duration: "6天5晚",
    avgCost: 3200,
    viewCount: 3156,
    favourNum: 234,
    commentCount: 89,
    authorLevel: "旅行达人",
    highlights: ["丽江古城", "玉龙雪山", "泸沽湖", "束河古镇"],
    tagList: ["丽江", "古城", "雪山", "文化"],
    user: {
      userName: "古城漫步者",
      userAvatar:
        "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
    },
    createTime: new Date(Date.now() - 86400000 * 15).toISOString()
  }
]);

// 获取攻略列表
const getPostList = async () => {
  try {
    const res = await listPostVoByPageUsingPost({
      searchText: searchText.value,
      current: paginationConfig.value.current,
      pageSize: paginationConfig.value.pageSize
    });

    // 处理API数据
    const apiData =
      (res.data as any)?.records?.map((item: any) => ({
        ...item,
        coverImage: require("@/assets/images/spot/photo" +
          ((item.id % 6) + 1) +
          ".png"),
        category: ["海岛度假", "文化古迹", "自然风光", "文化风情"][item.id % 4],
        duration: ["3天2晚", "4天3晚", "5天4晚", "6天5晚"][item.id % 4],
        avgCost: 1500 + (item.id % 10) * 200,
        viewCount: 1000 + (item.id % 50) * 100,
        commentCount: 10 + (item.id % 20) * 2,
        authorLevel: ["旅行达人", "资深驴友", "探险专家"][item.id % 3],
        highlights: ["精彩景点", "美食体验", "文化探索", "自然风光"].slice(
          0,
          2 + (item.id % 3)
        ),
        summary: item.content
          ? item.content.substring(0, 80) + "..."
          : "精彩的旅游攻略等你来发现..."
      })) || [];

    // 合并模拟数据和API数据
    postList.value = [...mockGuides.value, ...apiData];
    paginationConfig.value.total = postList.value.length;
  } catch (error) {
    // 如果API失败，使用模拟数据
    postList.value = mockGuides.value;
    paginationConfig.value.total = mockGuides.value.length;
    ElMessage.warning("使用本地数据展示");
  }
};

// 筛选后的攻略列表
const filteredGuides = computed(() => {
  let filtered = postList.value;

  // 根据筛选类型过滤
  if (activeFilter.value === "popular") {
    filtered = filtered.filter((guide) => guide.viewCount > 2000);
  } else if (activeFilter.value === "recent") {
    filtered = filtered.sort(
      (a, b) =>
        new Date(b.createTime).getTime() - new Date(a.createTime).getTime()
    );
  }

  // 根据搜索文本过滤
  if (searchText.value.trim()) {
    const searchLower = searchText.value.toLowerCase();
    filtered = filtered.filter(
      (guide) =>
        guide.title.toLowerCase().includes(searchLower) ||
        guide.summary.toLowerCase().includes(searchLower) ||
        guide.tagList.some((tag: string) =>
          tag.toLowerCase().includes(searchLower)
        )
    );
  }

  return filtered;
});

// 跳转到攻略详情页
const goToPostDetail = (postId: number) => {
  router.push({ name: "PostDetail", params: { id: postId } });
};

const showAddPost = () => {
  addPost.value = true;
};

// 处理筛选切换
const handleFilterChange = (filterKey: string) => {
  activeFilter.value = filterKey;
};

// 处理搜索
const handleSearch = () => {
  paginationConfig.value.current = 1;
};

// 处理分页
const handlePageChange = (page: number) => {
  paginationConfig.value.current = page;
};

const handleSizeChange = (val: number) => {
  paginationConfig.value.pageSize = val;
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = now.getTime() - date.getTime();
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 0) return "今天";
  if (diffDays === 1) return "昨天";
  if (diffDays < 7) return `${diffDays}天前`;
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

// 获取作者等级类型
const getAuthorLevelType = (level: string) => {
  const typeMap: Record<string, string> = {
    旅行达人: "success",
    资深驴友: "warning",
    探险专家: "danger",
    文化学者: "info",
    摄影达人: "primary"
  };
  return typeMap[level] || "info";
};

// 初始化加载攻略列表
onMounted(() => {
  getPostList();
  eventBus.on("refresh-post-list", getPostList);
});

onUnmounted(() => {
  eventBus.off("refresh-post-list", getPostList);
});
</script>

<style scoped lang="scss">
.travel-guide-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

// 页面头部
.guide-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 0 40px 0;
  text-align: center;
  position: relative;
  overflow: hidden;

  &::before {
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

  .header-content {
    position: relative;
    z-index: 1;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .page-title {
    font-size: 3rem;
    font-weight: bold;
    margin: 0 0 12px 0;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  }

  .page-subtitle {
    font-size: 1.2rem;
    opacity: 0.9;
    letter-spacing: 2px;
    margin: 0 0 32px 0;
  }

  .header-stats {
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
        opacity: 0.9;
      }
    }
  }
}

// 搜索筛选区域
.search-filter-section {
  max-width: 1200px;
  margin: 40px auto 60px auto;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;

  .search-bar {
    .el-input {
      max-width: 600px;
      margin: 0 auto;
      display: block;

      :deep(.el-input__wrapper) {
        border-radius: 25px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
    }
  }

  .filter-tabs {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;

    .filter-tab {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      background: white;
      border-radius: 25px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
      color: #666;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
      }

      &.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        transform: translateY(-2px);
      }
    }
  }

  .action-buttons {
    display: flex;
    justify-content: center;
  }
}

// 攻略列表
.guide-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 24px;
}

.guide-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  }

  .guide-cover {
    position: relative;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    &:hover img {
      transform: scale(1.05);
    }

    .guide-overlay {
      position: absolute;
      top: 16px;
      left: 16px;
      right: 16px;
      display: flex;
      justify-content: space-between;

      .guide-category {
        background: rgba(0, 0, 0, 0.7);
        color: white;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;
      }

      .guide-duration {
        background: rgba(255, 255, 255, 0.9);
        color: #333;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;
      }
    }
  }

  .guide-content {
    padding: 20px;

    .author-info {
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      .author-details {
        flex: 1;
        margin-left: 8px;

        .author-name {
          display: block;
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }

        .publish-time {
          font-size: 12px;
          color: #888;
        }
      }

      .author-level {
        margin-left: auto;
      }
    }

    .guide-title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin: 0 0 12px 0;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .guide-summary {
      color: #666;
      font-size: 14px;
      line-height: 1.6;
      margin: 0 0 16px 0;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .guide-highlights {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 16px;

      .highlight-tag {
        background: #f0f9ff;
        color: #0369a1;
        padding: 4px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;
      }
    }

    .guide-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 16px;

      .tag {
        margin: 0;
      }
    }

    .guide-stats {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .stat-group {
        display: flex;
        gap: 16px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #666;
          font-size: 14px;

          .el-icon {
            font-size: 16px;
          }
        }
      }

      .guide-price {
        text-align: right;

        .price-label {
          display: block;
          font-size: 12px;
          color: #888;
          margin-bottom: 2px;
        }

        .price-value {
          font-size: 18px;
          font-weight: bold;
          color: #ff6b35;
        }
      }
    }
  }
}

// 分页
.pagination {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

// 空状态
.empty-state {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  text-align: center;
}

// 响应式设计
@media (max-width: 768px) {
  .guide-header {
    padding: 40px 0 30px 0;

    .page-title {
      font-size: 2.2rem;
    }

    .page-subtitle {
      font-size: 1rem;
    }

    .header-stats {
      gap: 30px;

      .stat-item .stat-number {
        font-size: 2rem;
      }
    }
  }

  .guide-list {
    grid-template-columns: 1fr;
    padding: 0 16px;
  }

  .search-filter-section {
    padding: 0 16px;

    .filter-tabs {
      justify-content: flex-start;
      overflow-x: auto;
      padding-bottom: 8px;

      .filter-tab {
        flex-shrink: 0;
      }
    }
  }
}
</style>
