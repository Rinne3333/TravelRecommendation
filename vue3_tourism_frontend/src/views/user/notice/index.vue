<template>
  <div class="notice-container">
    <!-- 页面头部 -->
    <div class="notice-header">
      <div class="header-content">
        <h1 class="page-title">景区公告</h1>
        <p class="page-subtitle">SCENIC AREA NOTICES</p>
      </div>
    </div>

    <!-- 公告筛选栏 -->
    <div class="notice-filter">
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
    </div>

    <!-- 公告列表 -->
    <div class="notice-content">
      <div class="notice-grid">
        <!-- 重要公告卡片 -->
        <div
          v-for="notice in filteredNotices"
          :key="notice.id"
          class="notice-card"
        >
          <div class="card-header">
            <div class="notice-type" :class="notice.type">
              <el-icon><component :is="getTypeIcon(notice.type)" /></el-icon>
              <span>{{ getTypeLabel(notice.type) }}</span>
            </div>
            <div class="notice-date">{{ formatDate(notice.createTime) }}</div>
          </div>

          <div class="card-body">
            <h3 class="notice-title">
              {{
                notice.title || notice.noticeContent?.substring(0, 20) + "..."
              }}
            </h3>
            <p class="notice-content">{{ notice.noticeContent }}</p>
          </div>

          <div class="card-footer" v-if="notice.isImportant">
            <el-tag type="danger" size="small">
              <el-icon><Warning /></el-icon>
              重要提醒
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 入园须知专区 -->
      <div class="admission-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><InfoFilled /></el-icon>
            游客入园须知
          </h2>
          <p class="section-subtitle">VISITOR ADMISSION GUIDELINES</p>
        </div>

        <div class="admission-grid">
          <div
            v-for="(rule, index) in admissionRules"
            :key="index"
            class="admission-card"
          >
            <div class="rule-icon">
              <el-icon><component :is="rule.icon" /></el-icon>
            </div>
            <div class="rule-content">
              <h4 class="rule-title">{{ rule.title }}</h4>
              <p class="rule-desc">{{ rule.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 安全提示专区 -->
      <div class="safety-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Warning /></el-icon>
            安全提示
          </h2>
        </div>

        <div class="safety-tips">
          <div
            v-for="(tip, index) in safetyTips"
            :key="index"
            class="safety-tip"
          >
            <div class="tip-number">{{ index + 1 }}</div>
            <div class="tip-content">{{ tip }}</div>
          </div>
        </div>
      </div>

      <!-- 联系方式卡片 -->
      <div class="contact-section">
        <el-card class="contact-card">
          <div class="contact-header">
            <h3>
              <el-icon><Phone /></el-icon>
              联系我们
            </h3>
          </div>
          <div class="contact-info">
            <div class="contact-item">
              <span class="contact-label">客服热线：</span>
              <span class="contact-value">400-888-9999</span>
            </div>
            <div class="contact-item">
              <span class="contact-label">紧急救援：</span>
              <span class="contact-value emergency">120 / 110</span>
            </div>
            <div class="contact-item">
              <span class="contact-label">服务时间：</span>
              <span class="contact-value">08:00 - 18:00</span>
            </div>
          </div>
          <div class="qr-code">
            <img
              src="https://api.qrserver.com/v1/create-qr-code/?size=120x120&data=https://your-tourism.com"
              alt="景区官方二维码"
            />
            <p>扫码关注官方微信</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import { listNoticeVoByPageUsingPost } from "@/api/noticeController";
import { ElMessage } from "element-plus";
import { Warning, InfoFilled, Phone } from "@element-plus/icons-vue";

// 响应式数据
const noticeList = ref<any[]>([]);
const loading = ref(true);
const activeFilter = ref("all");

// 筛选标签
const filterTabs = ref([
  { key: "all", label: "全部公告", icon: "Bell" },
  { key: "important", label: "重要通知", icon: "Warning" },
  { key: "activity", label: "活动公告", icon: "InfoFilled" },
  { key: "safety", label: "安全提示", icon: "Warning" }
]);

// 入园须知规则
const admissionRules = ref([
  {
    title: "开放时间",
    description: "每日08:00-18:00开放，17:30停止入园",
    icon: "Clock"
  },
  {
    title: "门票预订",
    description: "建议提前网上预订，现场可能无票",
    icon: "Ticket"
  },
  {
    title: "身份验证",
    description: "入园需携带有效身份证件",
    icon: "User"
  },
  {
    title: "禁止拍照",
    description: "部分区域禁止拍照，请遵守规定",
    icon: "Camera"
  },
  {
    title: "停车指引",
    description: "景区提供停车场，按时收费",
    icon: "Location"
  },
  {
    title: "急救服务",
    description: "园区设有医务室，提供急救服务",
    icon: "FirstAidKit"
  }
]);

// 安全提示
const safetyTips = ref([
  "请勿在景区内吸烟，注意防火安全",
  "雨天路滑，请注意脚下安全，穿防滑鞋",
  "请勿攀爬栏杆、雕塑等设施",
  "请勿在水边嬉戏，注意防溺水",
  "请勿投喂动物，保持安全距离",
  "遇到紧急情况请及时联系工作人员",
  "请保管好个人财物，贵重物品随身携带",
  "老人儿童请在家人陪同下游览"
]);

// 获取公告列表
const getNoticeList = async () => {
  loading.value = true;
  try {
    const res = await listNoticeVoByPageUsingPost({
      current: 1,
      pageSize: 10
    });

    // 为API数据添加模拟的类型和重要性标识
    const processedData =
      (res.data as any)?.records?.map((item: any, index: number) => ({
        ...item,
        type: ["important", "activity", "safety"][index % 3],
        isImportant: index < 3,
        title: `景区公告 ${index + 1}`
      })) || [];

    // 合并API数据和模拟数据
    const mockNotices = [
      {
        id: "mock-1",
        title: "春节期间开放时间调整通知",
        noticeContent:
          "春节期间（2024年2月10日-2月17日）景区开放时间调整为09:00-17:00，请游客合理安排行程。",
        type: "important",
        isImportant: true,
        createTime: new Date().toISOString()
      },
      {
        id: "mock-2",
        title: "景区设施维护公告",
        noticeContent:
          "因设施维护需要，东区观光电梯将于本月15日-20日暂停使用，给您带来不便敬请谅解。",
        type: "important",
        isImportant: true,
        createTime: new Date(Date.now() - 86400000).toISOString()
      },
      {
        id: "mock-3",
        title: "春季赏花节活动开始",
        noticeContent:
          "春季赏花节正式开始！樱花、桃花、杏花竞相开放，欢迎游客前来观赏拍照。",
        type: "activity",
        isImportant: false,
        createTime: new Date(Date.now() - 172800000).toISOString()
      }
    ];

    noticeList.value = [...mockNotices, ...processedData];
    loading.value = false;
  } catch (error) {
    loading.value = false;
    // 使用模拟数据作为fallback
    noticeList.value = [
      {
        id: "fallback-1",
        title: "欢迎来到景区",
        noticeContent: "欢迎您来到我们的景区，请遵守相关规定，祝您游玩愉快！",
        type: "important",
        isImportant: true,
        createTime: new Date().toISOString()
      }
    ];
    ElMessage.warning("使用本地数据展示");
  }
};

// 筛选后的公告列表
const filteredNotices = computed(() => {
  if (activeFilter.value === "all") {
    return noticeList.value;
  }
  return noticeList.value.filter(
    (notice: any) => notice.type === activeFilter.value
  );
});

// 处理筛选切换
const handleFilterChange = (filterKey: string) => {
  activeFilter.value = filterKey;
};

// 获取类型图标
const getTypeIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    important: "Warning",
    activity: "InfoFilled",
    safety: "Warning"
  };
  return iconMap[type] || "Bell";
};

// 获取类型标签
const getTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    important: "重要通知",
    activity: "活动公告",
    safety: "安全提示"
  };
  return labelMap[type] || "一般公告";
};

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

// 组件挂载时获取数据
onMounted(() => {
  getNoticeList();
});
</script>

<style scoped lang="scss">
.notice-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding-bottom: 40px;
}

// 页面头部
.notice-header {
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
    margin: 0;
  }
}

// 筛选栏
.notice-filter {
  max-width: 1200px;
  margin: 24px auto 40px auto;
  padding: 0 20px;

  .filter-tabs {
    display: flex;
    justify-content: center;
    gap: 12px;
    flex-wrap: wrap;
  }

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

// 主要内容区域
.notice-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

// 公告网格
.notice-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
  margin-bottom: 60px;
}

.notice-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px 16px 24px;
    border-bottom: 1px solid #f0f0f0;

    .notice-type {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 500;

      &.important {
        background: #fef2f2;
        color: #dc2626;
      }

      &.activity {
        background: #eff6ff;
        color: #2563eb;
      }

      &.safety {
        background: #f0fdf4;
        color: #16a34a;
      }
    }

    .notice-date {
      color: #888;
      font-size: 14px;
    }
  }

  .card-body {
    padding: 20px 24px;

    .notice-title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin: 0 0 12px 0;
      line-height: 1.4;
    }

    .notice-content {
      color: #666;
      line-height: 1.6;
      margin: 0;
    }
  }

  .card-footer {
    padding: 16px 24px;
    border-top: 1px solid #f0f0f0;
  }
}

// 入园须知专区
.admission-section {
  margin-bottom: 60px;

  .section-header {
    text-align: center;
    margin-bottom: 40px;

    .section-title {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      font-size: 2.2rem;
      font-weight: bold;
      color: #333;
      margin: 0 0 8px 0;
    }

    .section-subtitle {
      color: #888;
      font-size: 1.1rem;
      letter-spacing: 1px;
      margin: 0;
    }
  }

  .admission-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 24px;
  }

  .admission-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: flex-start;
    gap: 16px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }

    .rule-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 20px;
      flex-shrink: 0;
    }

    .rule-content {
      flex: 1;

      .rule-title {
        font-size: 16px;
        font-weight: bold;
        color: #333;
        margin: 0 0 8px 0;
      }

      .rule-desc {
        color: #666;
        line-height: 1.5;
        margin: 0;
      }
    }
  }
}

// 安全提示专区
.safety-section {
  margin-bottom: 60px;

  .section-header {
    text-align: center;
    margin-bottom: 40px;

    .section-title {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      font-size: 2.2rem;
      font-weight: bold;
      color: #333;
      margin: 0;
    }
  }

  .safety-tips {
    background: white;
    border-radius: 16px;
    padding: 32px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);

    .safety-tip {
      display: flex;
      align-items: flex-start;
      gap: 16px;
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }

      .tip-number {
        width: 32px;
        height: 32px;
        background: linear-gradient(135deg, #ff9a56 0%, #ff6b35 100%);
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 14px;
        flex-shrink: 0;
      }

      .tip-content {
        color: #555;
        line-height: 1.6;
        padding-top: 4px;
      }
    }
  }
}

// 联系方式专区
.contact-section {
  .contact-card {
    border-radius: 16px;
    overflow: hidden;

    :deep(.el-card__body) {
      padding: 32px;
    }

    .contact-header {
      text-align: center;
      margin-bottom: 24px;

      h3 {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        font-size: 1.5rem;
        color: #333;
        margin: 0;
      }
    }

    .contact-info {
      margin-bottom: 32px;

      .contact-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .contact-label {
          color: #666;
          font-weight: 500;
        }

        .contact-value {
          font-weight: bold;
          color: #333;

          &.emergency {
            color: #dc2626;
            font-size: 18px;
          }
        }
      }
    }

    .qr-code {
      text-align: center;

      img {
        width: 120px;
        height: 120px;
        border-radius: 12px;
        border: 2px solid #e5e7eb;
        margin-bottom: 12px;
      }

      p {
        color: #666;
        margin: 0;
        font-size: 14px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .notice-header {
    padding: 40px 0 30px 0;

    .page-title {
      font-size: 2.2rem;
    }

    .page-subtitle {
      font-size: 1rem;
    }
  }

  .notice-grid {
    grid-template-columns: 1fr;
  }

  .admission-grid {
    grid-template-columns: 1fr;
  }

  .filter-tabs {
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 8px;

    .filter-tab {
      flex-shrink: 0;
    }
  }

  .admission-section .section-title,
  .safety-section .section-title {
    font-size: 1.8rem;
  }
}
</style>
