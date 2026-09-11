<template>
  <div class="account-page">
    <!-- 用户资料头部卡片 -->
    <div class="profile-header">
      <div class="profile-banner">
        <div class="banner-overlay"></div>
        <div class="profile-content">
          <div class="avatar-section">
            <div class="avatar-container">
              <img
                :src="user.userAvatar || defaultAvatar"
                :alt="user.userName"
                class="user-avatar"
              />
              <div class="avatar-badge">
                <el-icon><User /></el-icon>
              </div>
            </div>
          </div>
          <div class="user-info">
            <h1 class="user-name">{{ user.userName || "未设置昵称" }}</h1>
            <p class="user-role">
              <el-tag :type="getRoleType(user.userRole)" effect="light">
                {{ getRoleText(user.userRole) }}
              </el-tag>
            </p>
            <p class="user-id">ID: {{ user.id || "未知" }}</p>
            <p class="user-profile">
              {{ user.userProfile || "这个人很懒，什么都没留下..." }}
            </p>
          </div>
          <div class="stats-section">
            <div class="stat-item">
              <div class="stat-number">{{ spotList.length }}</div>
              <div class="stat-label">收藏景点</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ spotOrderList.length }}</div>
              <div class="stat-label">旅游订单</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ chartData.length }}</div>
              <div class="stat-label">旅行天数</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-tabs
        v-model="activeName"
        class="profile-tabs"
        type="card"
        @tab-change="handleTabChange"
      >
        <!-- 个人信息编辑 -->
        <el-tab-pane name="profile">
          <template #label>
            <span class="tab-label">
              <el-icon><User /></el-icon>
              个人信息
            </span>
          </template>
          <div class="tab-content">
            <div class="edit-profile-card">
              <div class="card-header">
                <h3>编辑个人资料</h3>
                <el-button
                  type="primary"
                  @click="updateUserInfo"
                  :loading="updating"
                >
                  <el-icon><Check /></el-icon>
                  保存修改
                </el-button>
              </div>
              <div class="edit-form">
                <el-row :gutter="32">
                  <el-col :span="12">
                    <div class="form-section">
                      <h4>基本信息</h4>
                      <el-form label-position="top" :model="user">
                        <el-form-item label="用户昵称">
                          <el-input
                            v-model="user.userName"
                            placeholder="请输入昵称"
                            prefix-icon="User"
                          />
                        </el-form-item>
                        <el-form-item label="个人简介">
                          <el-input
                            v-model="user.userProfile"
                            type="textarea"
                            :rows="4"
                            placeholder="介绍一下自己吧..."
                            maxlength="200"
                            show-word-limit
                          />
                        </el-form-item>
                      </el-form>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="form-section">
                      <h4>头像设置</h4>
                      <div class="avatar-upload">
                        <div class="current-avatar">
                          <img
                            :src="
                              newUserAvatar || user.userAvatar || defaultAvatar
                            "
                            alt="当前头像"
                            class="preview-avatar"
                          />
                        </div>
                        <el-form label-position="top">
                          <el-form-item label="头像链接">
                            <el-input
                              v-model="newUserAvatar"
                              placeholder="请输入图片链接"
                              prefix-icon="Picture"
                            />
                          </el-form-item>
                        </el-form>
                        <div class="avatar-tips">
                          <el-alert
                            title="建议使用 200x200 像素的正方形图片"
                            type="info"
                            :closable="false"
                            show-icon
                          />
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 收藏攻略 -->
        <el-tab-pane name="posts">
          <template #label>
            <span class="tab-label">
              <el-icon><Document /></el-icon>
              收藏攻略 ({{ favoritePostList.length }})
            </span>
          </template>
          <div class="tab-content">
            <div class="content-header">
              <h3>我的收藏攻略</h3>
              <p class="content-subtitle">您收藏的精彩旅游攻略</p>
            </div>
            <div v-if="favoritePostList.length === 0" class="empty-state">
              <el-empty description="暂无收藏的攻略">
                <el-button type="primary" @click="$router.push('/user/post')">
                  去发现攻略
                </el-button>
              </el-empty>
            </div>
            <div v-else class="posts-grid">
              <div
                v-for="post in favoritePostList"
                :key="post.id"
                class="post-card"
                @click="goToPostDetail(post.id)"
              >
                <div class="post-cover">
                  <img :src="getPostCover(post)" :alt="post.title" />
                  <div class="post-overlay">
                    <el-icon class="view-icon"><View /></el-icon>
                  </div>
                </div>
                <div class="post-info">
                  <h4 class="post-title">{{ post.title }}</h4>
                  <p class="post-summary">{{ getPostSummary(post) }}</p>
                  <div class="post-meta">
                    <span class="post-author">{{
                      post.user?.userName || "匿名用户"
                    }}</span>
                    <span class="post-stats">
                      <el-icon><Star /></el-icon>
                      {{ post.favourNum || 0 }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 收藏景点 -->
        <el-tab-pane name="spots">
          <template #label>
            <span class="tab-label">
              <el-icon><MapLocation /></el-icon>
              收藏景点 ({{ spotList.length }})
            </span>
          </template>
          <div class="tab-content">
            <div class="content-header">
              <h3>我的收藏景点</h3>
              <p class="content-subtitle">您收藏的精选旅游景点</p>
            </div>
            <div v-if="spotList.length === 0" class="empty-state">
              <el-empty description="暂无收藏的景点">
                <el-button type="primary" @click="$router.push('/user/spot')">
                  去发现景点
                </el-button>
              </el-empty>
            </div>
            <SpotList :spotList="spotList" v-else />
          </div>
        </el-tab-pane>

        <!-- 个人订单 -->
        <el-tab-pane name="orders">
          <template #label>
            <span class="tab-label">
              <el-icon><ShoppingBag /></el-icon>
              个人订单 ({{ spotOrderList.length }})
            </span>
          </template>
          <div class="tab-content">
            <div class="content-header">
              <h3>我的旅游订单</h3>
              <p class="content-subtitle">您的景点门票订单记录</p>
            </div>
            <div v-if="spotOrderList.length === 0" class="empty-state">
              <el-empty description="暂无订单记录">
                <el-button type="primary" @click="$router.push('/user/spot')">
                  去预订景点
                </el-button>
              </el-empty>
            </div>
            <SpotOrderList :spot-order-list="spotOrderList" v-else />
          </div>
        </el-tab-pane>

        <!-- 我的评论 -->
        <el-tab-pane name="comments">
          <template #label>
            <span class="tab-label">
              <el-icon><ChatDotRound /></el-icon>
              我的评论
            </span>
          </template>
          <div class="tab-content">
            <MyComment v-if="activeName === 'comments'" />
          </div>
        </el-tab-pane>

        <!-- 旅游日历 -->
        <el-tab-pane name="calendar">
          <template #label>
            <span class="tab-label">
              <el-icon><Calendar /></el-icon>
              旅游日历
            </span>
          </template>
          <div class="tab-content">
            <div class="calendar-card">
              <div class="calendar-header">
                <h3>{{ selectedYear }} 年旅行记录</h3>
                <el-select
                  v-model="selectedYear"
                  placeholder="选择年份"
                  style="width: 120px"
                >
                  <el-option label="2023" value="2023" />
                  <el-option label="2024" value="2024" />
                  <el-option label="2025" value="2025" />
                </el-select>
              </div>
              <HeatmapChart
                :data="chartData"
                :year="selectedYear"
                v-if="activeName === 'calendar'"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, defineAsyncComponent } from "vue";
import { GET_ID } from "@/utils/token";
import {
  getUserVoByIdUsingGet,
  updateMyUserUsingPost
} from "@/api/userController";
import { ElMessage } from "element-plus";
import {
  User,
  Document,
  MapLocation,
  ShoppingBag,
  ChatDotRound,
  Calendar,
  Check,
  // Picture,
  View,
  Star
} from "@element-plus/icons-vue";
import useUserStore from "@/store/modules/user";
import { listUserSpotFavoritesVoByPageUsingPost } from "@/api/userSpotFavoritesController";
import {
  getTravelDataUsingGet,
  listSpotOrderVoByPageUsingPost
} from "@/api/spotOrderController";
import { listMyFavourPostByPageUsingPost } from "@/api/postFavourController";
import { useRouter } from "vue-router";

// 动态导入组件
// const Post = defineAsyncComponent(() => import("@/components/Post/index.vue"));
const MyComment = defineAsyncComponent(
  () => import("@/components/MyComment/index.vue")
);
const SpotOrderList = defineAsyncComponent(
  () => import("@/components/SpotOrderList/index.vue")
);
const SpotList = defineAsyncComponent(
  () => import("@/components/SpotList/index.vue")
);
const HeatmapChart = defineAsyncComponent(
  () => import("@/components/CalendarChart/index.vue")
);

// 定义响应式数据
const activeName = ref("profile");
const userStore = useUserStore();
const router = useRouter();
const newUserAvatar = ref("");
const updating = ref(false);
const spotOrderList = ref<any[]>([]);
const favoritePostList = ref<any[]>([]);

// 默认头像
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

// 定义数据
const chartData = ref<{ date: string; value: number }[]>([]);
const selectedYear = ref("2025");
const user = ref({
  id: 0,
  userAvatar: "",
  userName: "",
  userProfile: "",
  userRole: ""
});

// 景点列表数据
const spotList = ref<any[]>([]);

// 获取角色类型
const getRoleType = (role: string) => {
  const roleMap: Record<string, string> = {
    admin: "danger",
    user: "primary",
    vip: "warning"
  };
  return roleMap[role] || "info";
};

// 获取角色文本
const getRoleText = (role: string) => {
  const roleMap: Record<string, string> = {
    admin: "管理员",
    user: "普通用户",
    vip: "VIP用户"
  };
  return roleMap[role] || "未知用户";
};

onMounted(() => {
  getUserInformationById();
  loadSpotList();
  loadSpotOrderList();
  loadFavoritePostList();
  fetchTravelData(Number(GET_ID()), 1);
});

// 调用后端接口获取数据
const fetchTravelData = async (userId: number, payStatus: number) => {
  try {
    const res = await getTravelDataUsingGet({
      userId,
      payStatus
    });
    if (res.data.code === 200) {
      // 临时使用示例数据，避免类型错误
      chartData.value = [
        { date: "2025-01-01", value: 1 },
        { date: "2025-01-15", value: 2 },
        { date: "2025-02-10", value: 1 }
      ];
    } else {
      // ElMessage.error("获取数据失败");
    }
  } catch (error: any) {
    ElMessage.error("请求失败");
    console.error(error);
  }
};

// 加载订单列表
const loadSpotOrderList = async () => {
  try {
    const res = await listSpotOrderVoByPageUsingPost({
      current: 1,
      pageSize: 10,
      userId: Number(GET_ID())
    });
    if (res.data.code === 200) {
      spotOrderList.value = res.data.data?.records ?? [];
    } else {
      // ElMessage.error("获取订单列表失败");
    }
  } catch (error) {
    ElMessage.error("获取订单列表失败");
    console.error(error);
  }
};

// 加载景点列表
const loadSpotList = async () => {
  try {
    const res = await listUserSpotFavoritesVoByPageUsingPost({
      current: 1,
      pageSize: 10,
      userId: Number(GET_ID()),
      status: 1
    });
    if (res.data?.code === 200) {
      spotList.value =
        res.data.data?.records?.map((item: any) => {
          return {
            ...item,
            id: item.spotId
          };
        }) || [];
    } else {
      // ElMessage.error("获取景点列表失败");
    }
  } catch (error) {
    ElMessage.error("获取景点列表失败");
    console.error(error);
  }
};

// 加载收藏攻略列表
const loadFavoritePostList = async () => {
  try {
    const res = await listMyFavourPostByPageUsingPost({
      current: 1,
      pageSize: 20
    });
    if ((res as any).code === 200) {
      favoritePostList.value = (res as any).data?.records || [];
      console.log("收藏攻略加载成功:", favoritePostList.value.length);
    } else {
      ElMessage.error("获取收藏攻略失败");
    }
  } catch (error) {
    ElMessage.error("获取收藏攻略失败");
    console.error(error);
  }
};

// 获取攻略封面图
const getPostCover = (post: any) => {
  // 使用攻略ID来生成对应的图片
  const imageIndex = (post.id % 6) + 1;
  return require(`@/assets/images/spot/photo${imageIndex}.png`);
};

// 获取攻略摘要
const getPostSummary = (post: any) => {
  if (post.content) {
    return post.content.length > 100
      ? post.content.substring(0, 100) + "..."
      : post.content;
  }
  return "精彩的旅游攻略等你来发现...";
};

// 跳转到攻略详情
const goToPostDetail = (postId: number) => {
  router.push({ name: "PostDetail", params: { id: postId } });
};

// 处理标签页切换
const handleTabChange = (tabName: string) => {
  console.log("切换到标签页:", tabName);

  // 根据不同的标签页刷新对应的数据
  switch (tabName) {
    case "posts":
      loadFavoritePostList();
      break;
    case "spots":
      loadSpotList();
      break;
    case "orders":
      loadSpotOrderList();
      break;
    case "calendar":
      fetchTravelData(Number(GET_ID()), 1);
      break;
    default:
      break;
  }
};

const updateUserInfo = async () => {
  updating.value = true;
  try {
    const res = await updateMyUserUsingPost({
      userAvatar: newUserAvatar.value || user.value.userAvatar,
      userName: user.value.userName,
      userProfile: user.value.userProfile
    });
    if (res.data?.code !== 200) {
      return ElMessage.error("更新用户信息失败");
    }
    // 调用 userStore 的 updateAvatar 方法
    await userStore.updateAvatar(newUserAvatar.value || user.value.userAvatar);
    ElMessage.success("更新用户信息成功");
    await getUserInformationById();
  } catch (error) {
    ElMessage.error("更新用户信息失败");
    console.error(error);
  } finally {
    updating.value = false;
  }
};

const getUserInformationById = async () => {
  const id = Number(GET_ID());
  try {
    let result: any = await getUserVoByIdUsingGet({
      id: id
    });
    if (result.code == 200) {
      user.value = result.data;
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};
</script>

<style lang="scss" scoped>
.account-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
}

.profile-header {
  position: relative;
  margin-bottom: 24px;
}

.profile-banner {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 0 0 24px 24px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    45deg,
    rgba(102, 126, 234, 0.8),
    rgba(118, 75, 162, 0.8)
  );
  backdrop-filter: blur(10px);
}

.profile-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  padding: 40px 32px;
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.user-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.3);
  object-fit: cover;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.05);
  }
}

.avatar-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: #409eff;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.user-info {
  flex: 1;
  color: white;
}

.user-name {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.user-role {
  margin: 0 0 8px 0;
}

.user-id {
  margin: 0 0 16px 0;
  opacity: 0.9;
  font-size: 14px;
}

.user-profile {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
  line-height: 1.6;
  max-width: 500px;
}

.stats-section {
  display: flex;
  gap: 32px;
  flex-shrink: 0;
}

.stat-item {
  text-align: center;
  color: white;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 24px;
}

.profile-tabs {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;

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
    border-radius: 8px 8px 0 0;
    margin-right: 8px;

    &.is-active {
      background: white;
      border-bottom-color: white;
    }
  }

  :deep(.el-tabs__content) {
    padding: 0;
  }
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-content {
  padding: 32px;
  min-height: 400px;
}

.edit-profile-card {
  background: white;
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;

  h3 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #1a202c;
  }
}

.edit-form {
  .form-section {
    h4 {
      margin: 0 0 20px 0;
      font-size: 16px;
      font-weight: 600;
      color: #2d3748;
      border-left: 4px solid #667eea;
      padding-left: 12px;
    }
  }
}

.avatar-upload {
  text-align: center;
}

.current-avatar {
  margin-bottom: 20px;
}

.preview-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e2e8f0;
  transition: all 0.3s ease;

  &:hover {
    border-color: #667eea;
    transform: scale(1.05);
  }
}

.avatar-tips {
  margin-top: 16px;
}

.calendar-card {
  background: white;
  border-radius: 12px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #1a202c;
  }
}

// 内容头部样式
.content-header {
  margin-bottom: 24px;
  text-align: center;

  h3 {
    margin: 0 0 8px 0;
    font-size: 24px;
    font-weight: 700;
    color: #1a202c;
  }

  .content-subtitle {
    margin: 0;
    color: #64748b;
    font-size: 16px;
  }
}

// 空状态样式
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

// 攻略网格布局
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  padding: 20px 0;
}

.post-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  }

  .post-cover {
    position: relative;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    .post-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.3));
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s ease;

      .view-icon {
        color: white;
        font-size: 32px;
      }
    }

    &:hover {
      img {
        transform: scale(1.05);
      }

      .post-overlay {
        opacity: 1;
      }
    }
  }

  .post-info {
    padding: 20px;

    .post-title {
      margin: 0 0 12px 0;
      font-size: 18px;
      font-weight: 600;
      color: #1a202c;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .post-summary {
      margin: 0 0 16px 0;
      color: #64748b;
      font-size: 14px;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .post-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .post-author {
        color: #64748b;
        font-size: 14px;
        font-weight: 500;
      }

      .post-stats {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #f59e0b;
        font-size: 14px;
        font-weight: 500;

        .el-icon {
          font-size: 16px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    text-align: center;
    padding: 24px 16px;
    gap: 24px;
  }

  .stats-section {
    justify-content: center;
    gap: 24px;
  }

  .user-name {
    font-size: 24px;
  }

  .tab-content {
    padding: 16px;
  }

  .edit-form {
    :deep(.el-row) {
      margin: 0;

      .el-col {
        margin-bottom: 24px;
      }
    }
  }

  .posts-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 16px 0;
  }

  .post-card {
    .post-cover {
      height: 160px;
    }

    .post-info {
      padding: 16px;

      .post-title {
        font-size: 16px;
      }

      .post-summary {
        font-size: 13px;
      }
    }
  }
}

// Element Plus 组件样式覆盖
:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 0 0 1px #667eea;
  }

  &.is-focus {
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  }
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    border-color: #667eea;
  }

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
  }
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;

  &.el-button--primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }
}

:deep(.el-tag) {
  border-radius: 20px;
  font-weight: 500;
}

:deep(.el-alert) {
  border-radius: 8px;
  border: none;
}
</style>
