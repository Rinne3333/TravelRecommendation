<template>
  <div class="ai-page-bg">
    <!-- 顶部Logo和欢迎语 -->
    <div class="ai-header">
      <img class="ai-logo" src="@/assets/images/logo.png" alt="logo" />
      <div class="ai-title">我是AI智能旅游助手，很高兴见到你！</div>
      <div class="ai-desc">
        我可以为您推荐旅游景点、规划行程、介绍当地文化，还能回答各种旅游相关问题~
      </div>
    </div>
    <!-- 对话区 -->
    <div class="ai-chat-card">
      <div class="chat-messages" ref="messagesRef">
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="[
            'message',
            message.role === 'user' ? 'user-message' : 'bot-message'
          ]"
        >
          <el-avatar
            v-if="message.role === 'assistant'"
            :size="40"
            src="https://pic.yupi.icu/5563/202502081502809.png"
            class="avatar"
          />
          <MdPreview
            class="message-content"
            editor-id="mdPreview"
            :modelValue="message.content"
            previewTheme="github"
            showCodeRowNumber
          />
          <el-avatar
            v-if="message.role === 'user'"
            :size="40"
            :src="GET_AVATAR()"
            class="avatar"
          />
        </div>
        <!-- 加载动画 -->
        <div v-if="isLoading" class="message bot-message">
          <el-avatar
            :size="40"
            src="https://pic.yupi.icu/5563/202502081502809.png"
            class="avatar"
          />
          <div class="message-content loading-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
            AI正在思考中...
          </div>
        </div>
      </div>
    </div>
    <!-- 输入区 -->
    <div class="ai-input-bar">
      <el-input
        type="textarea"
        :disabled="isLoading"
        v-model="inputMessage"
        placeholder="请输入您想咨询的旅游问题，比如：推荐北京的景点、三亚旅游攻略、如何规划5天的云南行程等"
        @keyup.enter.ctrl="sendMessage"
        class="ai-input"
        :autosize="{ minRows: 1, maxRows: 4 }"
      />
      <el-button
        type="primary"
        @click="sendMessage"
        :loading="isLoading"
        class="ai-send-btn"
        :disabled="!inputMessage.trim()"
        >发送</el-button
      >
    </div>
    <!-- 快捷提问 -->
    <div class="quick-questions" v-if="messages.length <= 1">
      <div class="quick-title">💡 快速开始</div>
      <div class="question-chips">
        <el-tag
          v-for="question in quickQuestions"
          :key="question"
          @click="askQuickQuestion(question)"
          class="question-chip"
          type="info"
          effect="plain"
        >
          {{ question }}
        </el-tag>
      </div>
    </div>

    <!-- 智能功能选择区 -->
    <div class="smart-actions" v-if="messages.length > 1">
      <div class="actions-title">🤖 智能助手功能</div>
      <div class="action-buttons">
        <div
          v-for="action in smartActions"
          :key="action.key"
          @click="handleSmartAction(action)"
          class="action-button"
          :class="{ loading: action.loading }"
        >
          <div class="action-icon">{{ action.icon }}</div>
          <div class="action-content">
            <div class="action-title">{{ action.title }}</div>
            <div class="action-desc">{{ action.description }}</div>
          </div>
          <div class="action-arrow">
            <el-icon v-if="!action.loading"><ArrowRight /></el-icon>
            <el-icon v-else class="rotating"><Loading /></el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { GET_AVATAR } from "@/utils/token";
import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import {
  chatWithDeepSeekStream,
  type ChatMessage
} from "@/api/deepseekController";
import { ElMessage, ElMessageBox } from "element-plus";
import { ArrowRight, Loading } from "@element-plus/icons-vue";

const router = useRouter();
const isLoading = ref(false);
const inputMessage = ref("");
const messagesRef = ref<HTMLElement | null>(null);
const messages = ref<ChatMessage[]>([]);

// 快捷提问选项
const quickQuestions = ref([
  "推荐北京必去的景点",
  "三亚5天旅游攻略",
  "云南丽江有什么好玩的",
  "上海迪士尼游玩指南",
  "西安美食推荐",
  "桂林山水游玩路线"
]);

// 智能功能选项
const smartActions = ref([
  {
    key: "route_planning",
    icon: "🗺️",
    title: "智能路线规划",
    description: "AI为您定制专属旅游路线",
    loading: false,
    prompt:
      "请根据我们之前的对话内容，为我制定一个详细的旅游路线规划。包括：1.推荐景点列表 2.最佳游览顺序 3.每个景点的游玩时间 4.景点间的交通方式 5.预估总费用。请以markdown格式输出，结构清晰。"
  },
  {
    key: "hotel_booking",
    icon: "🏨",
    title: "酒店预订建议",
    description: "推荐性价比最高的住宿",
    loading: false,
    prompt:
      "请根据我们讨论的旅游目的地，为我推荐合适的酒店住宿。包括：1.不同价位的酒店推荐(经济型、舒适型、豪华型) 2.酒店位置和交通便利性 3.酒店设施和服务特色 4.预订建议和注意事项 5.大概的价格范围。请以markdown格式输出。"
  },
  {
    key: "food_guide",
    icon: "🍜",
    title: "美食攻略",
    description: "发现当地特色美食",
    loading: false,
    prompt:
      "请为我推荐旅游目的地的特色美食。包括：1.必吃的当地特色菜 2.推荐的餐厅或小吃街 3.美食的特点和口味 4.价格参考 5.用餐建议和注意事项。请以markdown格式输出，让我能够充分体验当地美食文化。"
  },
  {
    key: "transport_guide",
    icon: "🚗",
    title: "交通出行指南",
    description: "最优交通方案推荐",
    loading: false,
    prompt:
      "请为我提供详细的交通出行指南。包括：1.到达目的地的交通方式对比 2.当地交通工具介绍 3.景点间的交通路线 4.交通费用预算 5.出行时间建议。请以markdown格式输出，帮我选择最适合的出行方案。"
  },
  {
    key: "budget_planning",
    icon: "💰",
    title: "预算规划",
    description: "制定合理的旅游预算",
    loading: false,
    prompt:
      "请帮我制定详细的旅游预算规划。包括：1.交通费用明细 2.住宿费用预算 3.餐饮费用估算 4.景点门票费用 5.购物和娱乐预算 6.应急费用建议。请以表格或清单形式用markdown输出，让我能够合理控制旅游开支。"
  },
  {
    key: "view_routes",
    icon: "📍",
    title: "查看推荐路线",
    description: "浏览精选旅游路线",
    loading: false,
    action: "navigate",
    route: "/user/spotRoute"
  }
]);

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return;

  const userMessage = inputMessage.value.trim();
  inputMessage.value = "";

  // 添加用户消息
  messages.value.push({ role: "user", content: userMessage });
  scrollToBottom();

  // 开始加载
  isLoading.value = true;

  // 添加AI消息占位符
  const aiMessageIndex = messages.value.length;
  messages.value.push({ role: "assistant", content: "" });

  try {
    // 准备发送给API的消息历史（包含系统提示）
    const apiMessages: ChatMessage[] = [
      {
        role: "system",
        content:
          "你是一个专业的旅游助手，名叫DeepSeek旅游助手。你的主要职责是：\n1. 为用户推荐旅游景点和路线\n2. 提供详细的旅游攻略和建议\n3. 介绍当地文化、美食、交通等信息\n4. 帮助用户规划行程\n5. 回答各种旅游相关问题\n\n请用友好、专业的语气回答用户问题，提供实用的旅游建议。回答要详细且有条理，可以使用markdown格式来美化回答。"
      },
      ...messages.value.slice(0, -1) // 排除刚添加的空消息
    ];

    // 调用DeepSeek流式API
    await chatWithDeepSeekStream(
      apiMessages,
      // 接收到新内容时的回调
      (content: string) => {
        messages.value[aiMessageIndex].content += content;
        scrollToBottom();
      },
      // 完成时的回调
      () => {
        isLoading.value = false;
        scrollToBottom();
      },
      // 错误时的回调
      (error: Error) => {
        isLoading.value = false;
        ElMessage.error({
          message: `AI服务暂时不可用: ${error.message}`,
          duration: 5000
        });
        // 移除失败的AI消息
        messages.value.splice(aiMessageIndex, 1);
      }
    );
  } catch (error) {
    isLoading.value = false;
    ElMessage.error({
      message: "发送消息失败，请检查网络连接",
      duration: 3000
    });
    // 移除失败的AI消息
    messages.value.splice(aiMessageIndex, 1);
  }
};

// 快捷提问
const askQuickQuestion = (question: string) => {
  if (isLoading.value) return;
  inputMessage.value = question;
  sendMessage();
};

// 处理智能功能
const handleSmartAction = async (action: any) => {
  if (isLoading.value || action.loading) return;

  // 如果是导航功能
  if (action.action === "navigate") {
    try {
      await ElMessageBox.confirm(
        `确定要跳转到${action.title}页面吗？`,
        "页面跳转确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "info"
        }
      );
      router.push(action.route);
    } catch {
      // 用户取消，不做任何操作
    }
    return;
  }

  // AI生成功能
  if (action.prompt) {
    action.loading = true;

    // 添加用户的功能请求消息
    messages.value.push({
      role: "user",
      content: `🤖 请求${action.title}`
    });
    scrollToBottom();

    // 开始加载
    isLoading.value = true;

    // 添加AI消息占位符
    const aiMessageIndex = messages.value.length;
    messages.value.push({ role: "assistant", content: "" });

    try {
      // 准备发送给API的消息历史（包含系统提示和功能特定提示）
      const apiMessages: ChatMessage[] = [
        {
          role: "system",
          content: `你是一个专业的旅游助手，名叫DeepSeek旅游助手。现在用户请求${action.title}服务。请根据之前的对话历史，提供专业、详细、实用的建议。回答要结构清晰，使用markdown格式美化输出。`
        },
        ...messages.value.slice(0, -2), // 排除刚添加的两条消息
        {
          role: "user",
          content: action.prompt
        }
      ];

      // 调用DeepSeek流式API
      await chatWithDeepSeekStream(
        apiMessages,
        // 接收到新内容时的回调
        (content: string) => {
          messages.value[aiMessageIndex].content += content;
          scrollToBottom();
        },
        // 完成时的回调
        () => {
          isLoading.value = false;
          action.loading = false;
          scrollToBottom();
          ElMessage.success(`${action.title}已生成完成！`);
        },
        // 错误时的回调
        (error: Error) => {
          isLoading.value = false;
          action.loading = false;
          ElMessage.error({
            message: `${action.title}生成失败: ${error.message}`,
            duration: 5000
          });
          // 移除失败的AI消息
          messages.value.splice(aiMessageIndex, 1);
        }
      );
    } catch (error) {
      isLoading.value = false;
      action.loading = false;
      ElMessage.error({
        message: `${action.title}请求失败，请检查网络连接`,
        duration: 3000
      });
      // 移除失败的AI消息
      messages.value.splice(aiMessageIndex, 1);
    }
  }
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
};

// 组件挂载时添加欢迎消息
onMounted(() => {
  messages.value.push({
    role: "assistant",
    content:
      "🌟 欢迎使用AI智能旅游助手！\n\n我可以为您提供：\n- 🎯 **景点推荐** - 根据您的喜好推荐合适的旅游目的地\n- 📋 **行程规划** - 帮您制定详细的旅游计划\n- 🍜 **美食指南** - 介绍当地特色美食\n- 🚗 **交通攻略** - 提供交通方式和路线建议\n- 🏨 **住宿推荐** - 推荐性价比高的住宿选择\n\n请告诉我您想去哪里旅游，或者有什么旅游相关的问题，我很乐意为您解答！"
  });
});
</script>

<style scoped>
.ai-page-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 40px 20px;
}

.ai-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  text-align: center;
}

.ai-logo {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.ai-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.ai-desc {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
  margin-bottom: 10px;
  max-width: 600px;
  line-height: 1.6;
}

.ai-chat-card {
  width: 100%;
  max-width: 900px;
  min-height: 500px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  padding: 32px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  max-height: 600px;
  padding-right: 8px;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 24px;
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.bot-message {
  flex-direction: row;
}

.avatar {
  flex-shrink: 0;
  border: 2px solid #f0f0f0;
}

.message-content {
  max-width: 80%;
  padding: 16px 20px;
  border-radius: 18px;
  margin: 0 12px;
  font-size: 1rem;
  line-height: 1.6;
  word-break: break-word;
}

.user-message .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.bot-message .message-content {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #e9ecef;
  border-bottom-left-radius: 6px;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;
  font-style: italic;
}

.typing-indicator {
  display: flex;
  gap: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #667eea;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%,
  80%,
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.ai-input-bar {
  width: 100%;
  max-width: 900px;
  display: flex;
  align-items: flex-end;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 16px 20px;
  margin-bottom: 20px;
  gap: 12px;
}

.ai-input {
  flex: 1;
  border-radius: 12px;
  font-size: 1rem;
}

.ai-input :deep(.el-textarea__inner) {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 12px 16px;
  font-size: 1rem;
  line-height: 1.5;
  resize: none;
}

.ai-input :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.ai-send-btn {
  border-radius: 12px;
  min-width: 80px;
  height: 44px;
  font-size: 1rem;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.ai-send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.ai-send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.quick-questions {
  width: 100%;
  max-width: 900px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.quick-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  text-align: center;
}

.question-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.question-chip {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 0.9rem;
  border: 1px solid #d9ecff;
  background: #f0f7ff;
  color: #409eff;
}

.question-chip:hover {
  background: #409eff;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.smart-actions {
  width: 100%;
  max-width: 900px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 24px;
  backdrop-filter: blur(10px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  margin-bottom: 20px;
}

.actions-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.action-button {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border: 2px solid #f0f2f5;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.action-button::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(102, 126, 234, 0.1),
    transparent
  );
  transition: left 0.5s ease;
}

.action-button:hover::before {
  left: 100%;
}

.action-button:hover {
  border-color: #667eea;
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.action-button.loading {
  opacity: 0.7;
  cursor: not-allowed;
  pointer-events: none;
}

.action-icon {
  font-size: 2.5rem;
  margin-right: 16px;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.action-content {
  flex: 1;
  text-align: left;
}

.action-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.3;
}

.action-desc {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
}

.action-arrow {
  margin-left: 12px;
  color: #667eea;
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.action-button:hover .action-arrow {
  transform: translateX(4px);
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-page-bg {
    padding: 20px 16px;
  }

  .ai-title {
    font-size: 1.8rem;
  }

  .ai-desc {
    font-size: 1rem;
  }

  .ai-chat-card {
    padding: 20px;
    min-height: 400px;
  }

  .message-content {
    max-width: 90%;
    font-size: 0.9rem;
    padding: 12px 16px;
  }

  .question-chips {
    gap: 8px;
  }

  .question-chip {
    font-size: 0.8rem;
    padding: 6px 12px;
  }

  .smart-actions {
    padding: 16px;
    margin-bottom: 16px;
  }

  .actions-title {
    font-size: 1rem;
    margin-bottom: 16px;
  }

  .action-buttons {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .action-button {
    padding: 16px;
  }

  .action-icon {
    font-size: 2rem;
    margin-right: 12px;
  }

  .action-title {
    font-size: 1rem;
  }

  .action-desc {
    font-size: 0.85rem;
  }
}
</style>
