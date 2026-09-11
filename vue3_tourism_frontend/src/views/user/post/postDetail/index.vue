<template>
  <div class="guide-detail-container">
    <!-- 返回导航 -->
    <div class="detail-nav">
      <el-button @click="goBack" :icon="ArrowLeft" type="primary" plain>
        返回攻略列表
      </el-button>
    </div>

    <!-- 攻略头部信息 -->
    <div class="guide-hero">
      <div class="hero-background">
        <img :src="guide.coverImage" :alt="guide.title" />
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <div class="breadcrumb">
          <span>旅游攻略</span>
          <el-icon><ArrowRight /></el-icon>
          <span>{{ guide.category }}</span>
        </div>
        <h1 class="guide-title">{{ guide.title }}</h1>
        <p class="guide-summary">{{ guide.summary }}</p>
        <div class="guide-meta">
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>{{ guide.duration }}</span>
          </div>
          <div class="meta-item">
            <el-icon><User /></el-icon>
            <span>人均 ¥{{ guide.avgCost }}</span>
          </div>
          <div class="meta-item">
            <el-icon><View /></el-icon>
            <span>{{ viewCount }} 浏览</span>
          </div>
          <div class="meta-item">
            <el-icon><Star /></el-icon>
            <span>{{ collectCount }} 收藏</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="guide-main">
      <!-- 左侧内容 -->
      <div class="guide-content">
        <!-- 数据统计卡片 -->
        <div class="stats-card">
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><View /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-number">{{
                  viewCount.toLocaleString()
                }}</span>
                <span class="stat-label">浏览量</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><StarFilled /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-number">{{ collectCount }}</span>
                <span class="stat-label">收藏数</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><Star /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-number">{{ likeCount }}</span>
                <span class="stat-label">点赞数</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-number">{{ guide.commentCount }}</span>
                <span class="stat-label">评论数</span>
              </div>
            </div>
          </div>
        </div>
        <!-- 作者信息卡片 -->
        <div class="author-card">
          <div class="author-info">
            <el-avatar :src="guide.user?.userAvatar" :size="60" />
            <div class="author-details">
              <h3 class="author-name">{{ guide.user?.userName }}</h3>
              <p class="author-level">{{ guide.authorLevel }}</p>
              <p class="publish-info">
                发布于 {{ formatDate(guide.createTime) }}
              </p>
            </div>
          </div>
          <div class="author-stats">
            <div class="stat-item">
              <span class="stat-number">{{
                guide.authorStats?.guides || 12
              }}</span>
              <span class="stat-label">攻略</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{
                guide.authorStats?.followers || 156
              }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{
                guide.authorStats?.likes || 892
              }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>
        </div>

        <!-- 攻略亮点 -->
        <div class="highlights-section">
          <h3 class="section-title">
            <el-icon><Trophy /></el-icon>
            攻略亮点
          </h3>
          <div class="highlights-grid">
            <div
              v-for="highlight in guide.highlights"
              :key="highlight"
              class="highlight-item"
            >
              <el-icon><Check /></el-icon>
              <span>{{ highlight }}</span>
            </div>
          </div>
        </div>

        <!-- 攻略正文 -->
        <div class="guide-body">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            详细攻略
          </h3>
          <MdPreview
            class="markdown-content"
            editor-id="mdPreview"
            :modelValue="guide.content"
            previewTheme="github"
            showCodeRowNumber
          />
        </div>

        <!-- 标签区域 -->
        <div class="tags-section">
          <h3 class="section-title">相关标签</h3>
          <div class="tags-list">
            <el-tag
              v-for="tag in guide.tagList"
              :key="tag"
              size="large"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-section">
          <div class="action-buttons">
            <el-button
              :type="initLikeStatus === 1 ? 'danger' : 'primary'"
              size="large"
              @click="doThumb"
              :loading="likeLoading"
              :disabled="likeLoading"
            >
              <template #icon v-if="!likeLoading">
                <el-icon :color="initLikeStatus === 1 ? '#ff4757' : '#3742fa'">
                  <component
                    :is="initLikeStatus === 1 ? 'StarFilled' : 'Star'"
                  />
                </el-icon>
              </template>
              {{ initLikeStatus === 1 ? "已点赞" : "点赞" }} ({{ likeCount }})
            </el-button>
            <el-button
              :type="initCollectStatus === 1 ? 'warning' : 'success'"
              size="large"
              @click="handleCollect"
              :loading="collectLoading"
              :disabled="collectLoading"
            >
              <template #icon v-if="!collectLoading">
                <el-icon
                  :color="initCollectStatus === 1 ? '#ffa502' : '#2ed573'"
                >
                  <component
                    :is="initCollectStatus === 1 ? 'StarFilled' : 'Star'"
                  />
                </el-icon>
              </template>
              {{ initCollectStatus === 1 ? "已收藏" : "收藏" }} ({{
                collectCount
              }})
            </el-button>
            <el-button
              type="info"
              size="large"
              @click="handleShare"
              :icon="Share"
            >
              分享攻略
            </el-button>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <div class="guide-sidebar">
        <!-- 行程概览 -->
        <el-card class="sidebar-card">
          <template #header>
            <h4>
              <el-icon><MapLocation /></el-icon>
              行程概览
            </h4>
          </template>
          <div class="itinerary-overview">
            <div class="overview-item">
              <span class="label">出行天数：</span>
              <span class="value">{{ guide.duration }}</span>
            </div>
            <div class="overview-item">
              <span class="label">人均消费：</span>
              <span class="value price">¥{{ guide.avgCost }}</span>
            </div>
            <div class="overview-item">
              <span class="label">最佳时间：</span>
              <span class="value">{{ guide.bestTime || "四季皆宜" }}</span>
            </div>
            <div class="overview-item">
              <span class="label">适合人群：</span>
              <span class="value">{{ guide.suitableFor || "所有人群" }}</span>
            </div>
          </div>
        </el-card>

        <!-- 推荐攻略 -->
        <el-card class="sidebar-card">
          <template #header>
            <h4>
              <el-icon><Star /></el-icon>
              相关推荐
            </h4>
          </template>
          <div class="related-guides">
            <div
              v-for="item in relatedGuides"
              :key="item.id"
              class="related-item"
              @click="goToGuide(item.id)"
            >
              <img :src="item.coverImage" :alt="item.title" />
              <div class="related-info">
                <h5 class="related-title">{{ item.title }}</h5>
                <p class="related-meta">{{ item.viewCount }} 浏览</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comments-section">
      <CommentView :postId="postId" />
    </div>

    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享攻略" width="500px">
      <div class="share-dialog-content">
        <!-- 分享链接 -->
        <div class="share-section">
          <h4>分享链接</h4>
          <el-card>
            <div class="link-container">
              <span class="share-url">{{ currentPageUrl }}</span>
              <el-button type="primary" @click="copyLink">复制链接</el-button>
            </div>
          </el-card>
        </div>

        <!-- 二维码分享 -->
        <div class="share-section">
          <h4>扫码分享</h4>
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
            <p>扫描二维码分享攻略</p>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineAsyncComponent } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  ArrowLeft,
  ArrowRight,
  Calendar,
  User,
  View,
  Star,
  StarFilled,
  Trophy,
  Check,
  Document,
  Share,
  MapLocation,
  ChatDotRound
} from "@element-plus/icons-vue";
import { getPostVoByIdUsingGet } from "@/api/postController";
import { doPostFavourUsingPost } from "@/api/postFavourController";
import { ElMessage } from "element-plus";
import { doThumbUsingPost } from "@/api/postThumbController";
import QRCodeVue3 from "qrcode-vue3";
import useClipboard from "vue-clipboard3";
import { MdPreview } from "md-editor-v3";

// 导入评论组件
const CommentView = defineAsyncComponent(
  () => import("@/components/CommentView/index.vue")
);

// 获取路由参数
const route = useRoute();
const router = useRouter();
const postId = Number(route.params.id) || 1;

// 分享对话框的显示状态
const shareDialogVisible = ref(false);
// 当前页面地址
const currentPageUrl = ref(window.location.href);

// 攻略详情数据
const guide = ref({
  id: postId,
  title: "精彩旅游攻略",
  thumbNum: 126,
  content: `# 完整旅游攻略

## 行程安排

### 第一天：抵达目的地
- **上午**：抵达机场/车站，前往酒店办理入住
- **下午**：休息调整，熟悉周边环境
- **晚上**：品尝当地特色美食

### 第二天：主要景点游览
- **上午**：游览标志性景点，拍照留念
- **下午**：深度体验当地文化
- **晚上**：观看特色表演或夜景

### 第三天：自然风光探索
- **上午**：前往自然景区
- **下午**：户外活动体验
- **晚上**：温泉或SPA放松

## 美食推荐

### 必尝美食
1. **当地特色菜** - 传统口味，不容错过
2. **街头小吃** - 地道风味，价格实惠
3. **高档餐厅** - 精致料理，适合特殊场合

### 推荐餐厅
- 老字号餐厅：历史悠久，口碑极佳
- 网红打卡店：环境优美，适合拍照
- 当地人推荐：真正的本地味道

## 住宿建议

### 酒店选择
- **豪华酒店**：服务一流，设施完善
- **精品民宿**：特色鲜明，体验独特
- **经济型酒店**：性价比高，干净舒适

### 位置考虑
- 市中心：交通便利，购物方便
- 景区附近：游览方便，环境优美
- 商务区：设施齐全，适合商务出行

## 交通指南

### 到达方式
- 飞机：最快捷，适合远距离
- 高铁：舒适便利，准点率高
- 自驾：自由度高，适合家庭出游

### 当地交通
- 地铁/公交：经济实惠，覆盖面广
- 出租车/网约车：便捷快速，适合短距离
- 租车：灵活自由，适合深度游

## 购物指南

### 特产推荐
- 当地工艺品：纪念价值高
- 特色食品：可以带回家分享
- 传统服饰：文化特色浓厚

### 购物地点
- 传统市场：价格实惠，体验当地生活
- 商业街区：品牌齐全，购物环境好
- 免税店：价格优惠，品质保证

## 注意事项

### 安全提醒
- 保管好个人财物
- 注意交通安全
- 了解当地法律法规

### 文化尊重
- 尊重当地风俗习惯
- 保护环境，文明旅游
- 与当地人友好交流

## 费用预算

### 基本费用
- 交通费：根据距离和方式而定
- 住宿费：根据标准和位置而定
- 餐饮费：根据消费水平而定

### 可选费用
- 景点门票
- 娱乐活动
- 购物消费

## 最佳旅行时间

根据目的地的气候特点，选择最适合的旅行时间，避开极端天气和旅游旺季，享受更好的旅行体验。

---

希望这份攻略对您的旅行有所帮助！祝您旅途愉快！`,
  summary:
    "这是一份详细的旅游攻略，包含行程安排、美食推荐、住宿建议、交通指南等完整信息，帮助您规划完美的旅行。",
  coverImage: require("@/assets/images/spot/photo1.png"),
  category: "综合攻略",
  duration: "3天2晚",
  avgCost: 2500,
  viewCount: 1856,
  favourNum: 126,
  commentCount: 34,
  authorLevel: "旅行达人",
  highlights: ["详细行程", "美食推荐", "住宿指南", "交通攻略"],
  tagList: ["旅游", "攻略", "自由行", "度假"],
  bestTime: "四季皆宜",
  suitableFor: "所有人群",
  user: {
    userName: "旅行专家",
    userAvatar:
      "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
  },
  createTime: new Date().toISOString(),
  authorStats: {
    guides: 12,
    followers: 156,
    likes: 892
  }
});

// 相关推荐攻略
const relatedGuides = ref([
  {
    id: 2,
    title: "北京故宫-长城经典3日游",
    coverImage: require("@/assets/images/spot/photo2.png"),
    viewCount: 3247
  },
  {
    id: 3,
    title: "张家界天门山玻璃栈道",
    coverImage: require("@/assets/images/spot/photo3.png"),
    viewCount: 1892
  },
  {
    id: 4,
    title: "西安兵马俑-华清宫历史游",
    coverImage: require("@/assets/images/spot/photo4.png"),
    viewCount: 2134
  }
]);

// 点赞和收藏计数
const likeCount = ref(126);
const collectCount = ref(89);
const initCollectStatus = ref(0);
const initLikeStatus = ref(0);
const viewCount = ref(1856);

// 加载状态
const likeLoading = ref(false);
const collectLoading = ref(false);

// 获取攻略详情
const fetchPostDetail = async () => {
  try {
    const response = await getPostVoByIdUsingGet({ id: postId });
    if ((response as any)?.data) {
      const data = (response as any).data;

      // 更新攻略数据
      guide.value = {
        ...guide.value,
        id: data.id || postId,
        title: data.title || guide.value.title,
        content: data.content || guide.value.content,
        createTime: data.createTime || guide.value.createTime,
        favourNum: data.favourNum || guide.value.favourNum,
        thumbNum: data.thumbNum || guide.value.thumbNum,
        user: {
          userName: data.user?.userName || guide.value.user.userName,
          userAvatar: data.user?.userAvatar || guide.value.user.userAvatar
        }
      };

      // 更新计数器
      likeCount.value = data.thumbNum || likeCount.value;
      collectCount.value = data.favourNum || collectCount.value;
      viewCount.value = guide.value.viewCount;

      // 获取用户收藏和点赞状态
      initCollectStatus.value = data.hasFavour ? 1 : 0;
      initLikeStatus.value = data.hasThumb ? 1 : 0;

      console.log("攻略详情获取成功:", {
        id: data.id,
        title: data.title,
        favourNum: data.favourNum,
        thumbNum: data.thumbNum,
        hasFavour: data.hasFavour,
        hasThumb: data.hasThumb
      });
    }
  } catch (error) {
    console.error("获取攻略详情失败:", error);
    ElMessage.warning("使用默认攻略数据展示");
  }
};

// 点赞处理
const doThumb = async () => {
  if (likeLoading.value) return;

  try {
    likeLoading.value = true;
    console.log("点赞操作开始:", {
      postId,
      currentStatus: initLikeStatus.value
    });

    const response = await doThumbUsingPost({
      postId: postId
    });
    console.log("点赞API响应:", response);

    // 切换点赞状态
    if (initLikeStatus.value === 0) {
      initLikeStatus.value = 1;
      likeCount.value += 1;
      guide.value.thumbNum = likeCount.value;
      ElMessage.success("点赞成功");
    } else {
      initLikeStatus.value = 0;
      likeCount.value -= 1;
      guide.value.thumbNum = likeCount.value;
      ElMessage.success("取消点赞成功");
    }

    console.log("点赞状态更新完成:", {
      newStatus: initLikeStatus.value,
      newCount: likeCount.value
    });
  } catch (error) {
    console.error("点赞操作失败:", error);
    ElMessage.error("点赞操作失败，请稍后重试");
  } finally {
    likeLoading.value = false;
  }
};

// 收藏处理
const handleCollect = async () => {
  if (collectLoading.value) return;

  try {
    collectLoading.value = true;
    console.log("收藏操作开始:", {
      postId,
      currentStatus: initCollectStatus.value
    });

    const response = await doPostFavourUsingPost({ postId: postId });
    console.log("收藏API响应:", response);

    // 切换收藏状态
    if (initCollectStatus.value === 0) {
      initCollectStatus.value = 1;
      collectCount.value += 1;
      guide.value.favourNum = collectCount.value;
      ElMessage.success("收藏成功");
    } else {
      initCollectStatus.value = 0;
      collectCount.value -= 1;
      guide.value.favourNum = collectCount.value;
      ElMessage.success("取消收藏成功");
    }

    console.log("收藏状态更新完成:", {
      newStatus: initCollectStatus.value,
      newCount: collectCount.value
    });
  } catch (error) {
    console.error("收藏操作失败:", error);
    ElMessage.error("收藏操作失败，请稍后重试");
  } finally {
    collectLoading.value = false;
  }
};

// 复制链接
const { toClipboard } = useClipboard();
const copyLink = async () => {
  try {
    await toClipboard(currentPageUrl.value);
    ElMessage.success("链接已复制到剪贴板");
  } catch (e) {
    ElMessage.error("复制失败");
  }
};

// 分享处理
const handleShare = () => {
  shareDialogVisible.value = true;
};

// 返回攻略列表
const goBack = () => {
  router.push("/user/post");
};

// 跳转到其他攻略
const goToGuide = (id: number) => {
  router.push(`/user/post/detail/${id}`);
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
};

// 增加浏览量统计
const incrementViewCount = () => {
  try {
    // 检查是否已经统计过浏览量（避免重复刷新页面增加浏览量）
    const viewedKey = `post_viewed_${postId}`;
    const lastViewTime = localStorage.getItem(viewedKey);
    const now = Date.now();

    // 如果超过5分钟没有浏览过，则增加浏览量
    if (!lastViewTime || now - parseInt(lastViewTime) > 5 * 60 * 1000) {
      viewCount.value += 1;
      guide.value.viewCount = viewCount.value;
      localStorage.setItem(viewedKey, now.toString());
      console.log("浏览量已增加:", viewCount.value);
    }
  } catch (error) {
    console.error("增加浏览量失败:", error);
  }
};

// 在组件挂载时获取数据
onMounted(async () => {
  await fetchPostDetail();
  // 增加浏览量统计
  incrementViewCount();
});
</script>

<style scoped lang="scss">
.guide-detail-container {
  min-height: 100vh;
  background: #f8fafc;
}

// 导航栏
.detail-nav {
  background: white;
  padding: 16px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .el-button {
    margin-left: 20px;
  }
}

// 攻略头部
.guide-hero {
  position: relative;
  height: 400px;
  overflow: hidden;

  .hero-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .hero-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(
        to bottom,
        rgba(0, 0, 0, 0.3),
        rgba(0, 0, 0, 0.6)
      );
    }
  }

  .hero-content {
    position: absolute;
    bottom: 40px;
    left: 0;
    right: 0;
    color: white;
    text-align: center;
    z-index: 2;

    .breadcrumb {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      margin-bottom: 16px;
      opacity: 0.9;

      span {
        font-size: 14px;
      }
    }

    .guide-title {
      font-size: 3rem;
      font-weight: bold;
      margin: 0 0 16px 0;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    }

    .guide-summary {
      font-size: 1.2rem;
      margin: 0 0 24px 0;
      opacity: 0.95;
      max-width: 800px;
      margin-left: auto;
      margin-right: auto;
    }

    .guide-meta {
      display: flex;
      justify-content: center;
      gap: 32px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 16px;

        .el-icon {
          font-size: 18px;
        }
      }
    }
  }
}

// 主要内容区域
.guide-main {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 40px;
}

// 左侧内容
.guide-content {
  .stats-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-bottom: 32px;

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: linear-gradient(135deg, #f8fafc, #e2e8f0);
        border-radius: 12px;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
        }

        .stat-icon {
          width: 48px;
          height: 48px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 12px;
          background: linear-gradient(135deg, #667eea, #764ba2);

          .el-icon {
            font-size: 24px;
            color: white;
          }
        }

        .stat-info {
          display: flex;
          flex-direction: column;

          .stat-number {
            font-size: 24px;
            font-weight: bold;
            color: #333;
            line-height: 1;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            color: #666;
            font-weight: 500;
          }
        }

        &:nth-child(1) .stat-icon {
          background: linear-gradient(135deg, #667eea, #764ba2);
        }

        &:nth-child(2) .stat-icon {
          background: linear-gradient(135deg, #ffa502, #ff6348);
        }

        &:nth-child(3) .stat-icon {
          background: linear-gradient(135deg, #ff6b6b, #ee5a24);
        }

        &:nth-child(4) .stat-icon {
          background: linear-gradient(135deg, #2ed573, #1e90ff);
        }
      }
    }
  }
  .author-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-bottom: 32px;

    .author-info {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      .author-details {
        margin-left: 16px;
        flex: 1;

        .author-name {
          font-size: 20px;
          font-weight: bold;
          color: #333;
          margin: 0 0 4px 0;
        }

        .author-level {
          font-size: 14px;
          color: #667eea;
          font-weight: 500;
          margin: 0 0 4px 0;
        }

        .publish-info {
          font-size: 14px;
          color: #888;
          margin: 0;
        }
      }
    }

    .author-stats {
      display: flex;
      justify-content: space-around;

      .stat-item {
        text-align: center;

        .stat-number {
          display: block;
          font-size: 24px;
          font-weight: bold;
          color: #333;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #888;
        }
      }
    }
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin: 0 0 20px 0;

    .el-icon {
      color: #667eea;
    }
  }

  .highlights-section {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-bottom: 32px;

    .highlights-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;

      .highlight-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px;
        background: #f0f9ff;
        border-radius: 8px;
        color: #0369a1;
        font-weight: 500;

        .el-icon {
          color: #10b981;
        }
      }
    }
  }

  .guide-body {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-bottom: 32px;

    .markdown-content {
      :deep(.md-editor-preview) {
        padding: 0;
        background: transparent;
      }
    }
  }

  .tags-section {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-bottom: 32px;

    .tags-list {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;

      .tag-item {
        margin: 0;
      }
    }
  }

  .action-section {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

    .action-buttons {
      display: flex;
      justify-content: center;
      gap: 16px;

      .el-button {
        min-width: 140px;
        height: 48px;
        font-weight: 500;
        border-radius: 24px;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        }

        &.is-disabled {
          opacity: 0.7;
          transform: none;
        }

        &.el-button--danger {
          background: linear-gradient(135deg, #ff6b6b, #ee5a24);
          border: none;

          &:hover {
            background: linear-gradient(135deg, #ff5252, #d63031);
          }
        }

        &.el-button--warning {
          background: linear-gradient(135deg, #ffa502, #ff6348);
          border: none;

          &:hover {
            background: linear-gradient(135deg, #ff9500, #e55039);
          }
        }

        &.el-button--primary {
          background: linear-gradient(135deg, #667eea, #764ba2);
          border: none;

          &:hover {
            background: linear-gradient(135deg, #5a67d8, #6b46c1);
          }
        }

        &.el-button--success {
          background: linear-gradient(135deg, #2ed573, #1e90ff);
          border: none;

          &:hover {
            background: linear-gradient(135deg, #26de81, #0984e3);
          }
        }
      }
    }
  }
}

// 右侧边栏
.guide-sidebar {
  .sidebar-card {
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;

    :deep(.el-card__header) {
      padding: 20px 24px 16px 24px;
      border-bottom: 1px solid #f0f0f0;

      h4 {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0;
        font-size: 16px;
        font-weight: bold;
        color: #333;

        .el-icon {
          color: #667eea;
        }
      }
    }

    :deep(.el-card__body) {
      padding: 20px 24px;
    }
  }

  .itinerary-overview {
    .overview-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #666;
        font-weight: 500;
      }

      .value {
        font-weight: bold;
        color: #333;

        &.price {
          color: #ff6b35;
          font-size: 18px;
        }
      }
    }
  }

  .related-guides {
    .related-item {
      display: flex;
      gap: 12px;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: background 0.3s ease;
      border-radius: 8px;
      margin-bottom: 8px;

      &:hover {
        background: #f8fafc;
      }

      &:last-child {
        border-bottom: none;
        margin-bottom: 0;
      }

      img {
        width: 60px;
        height: 45px;
        object-fit: cover;
        border-radius: 6px;
      }

      .related-info {
        flex: 1;

        .related-title {
          font-size: 14px;
          font-weight: 500;
          color: #333;
          margin: 0 0 4px 0;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .related-meta {
          font-size: 12px;
          color: #888;
          margin: 0;
        }
      }
    }
  }
}

// 评论区
.comments-section {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

// 分享对话框
.share-dialog-content {
  .share-section {
    margin-bottom: 24px;

    h4 {
      margin: 0 0 12px 0;
      color: #333;
    }

    .link-container {
      display: flex;
      align-items: center;
      gap: 12px;

      .share-url {
        flex: 1;
        padding: 8px;
        background: #f8fafc;
        border-radius: 6px;
        font-size: 14px;
        color: #666;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .qr-container {
      text-align: center;

      p {
        margin: 12px 0 0 0;
        color: #666;
        font-size: 14px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .guide-hero {
    height: 300px;

    .hero-content {
      .guide-title {
        font-size: 2rem;
      }

      .guide-summary {
        font-size: 1rem;
      }

      .guide-meta {
        flex-direction: column;
        gap: 12px;
      }
    }
  }

  .guide-main {
    grid-template-columns: 1fr;
    gap: 24px;
    padding: 0 16px;
  }

  .guide-sidebar {
    order: -1;
  }

  .action-section .action-buttons {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .stats-card .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .stat-item {
      padding: 12px;

      .stat-icon {
        width: 40px;
        height: 40px;

        .el-icon {
          font-size: 20px;
        }
      }

      .stat-info {
        .stat-number {
          font-size: 20px;
        }

        .stat-label {
          font-size: 12px;
        }
      }
    }
  }
}
</style>
