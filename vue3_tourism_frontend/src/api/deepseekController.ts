// DeepSeek API - 直接使用fetch，不依赖项目的request工具

// DeepSeek API配置
const DEEPSEEK_API_KEY = process.env.VUE_APP_DEEPSEEK_API_KEY;
const DEEPSEEK_BASE_URL = 'https://api.deepseek.com';

// 消息类型定义
export interface ChatMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
}

// API请求参数
export interface ChatCompletionRequest {
  model: string;
  messages: ChatMessage[];
  temperature?: number;
  max_tokens?: number;
  stream?: boolean;
}

// API响应类型
export interface ChatCompletionResponse {
  id: string;
  object: string;
  created: number;
  model: string;
  choices: {
    index: number;
    message: ChatMessage;
    finish_reason: string;
  }[];
  usage: {
    prompt_tokens: number;
    completion_tokens: number;
    total_tokens: number;
  };
}

// 调用DeepSeek聊天API
export const chatWithDeepSeek = async (messages: ChatMessage[]): Promise<ChatCompletionResponse> => {
  if (!DEEPSEEK_API_KEY) {
    throw new Error('Missing VUE_APP_DEEPSEEK_API_KEY');
  }

  const requestData: ChatCompletionRequest = {
    model: 'deepseek-chat',
    messages: messages,
    temperature: 0.7,
    max_tokens: 2000,
    stream: false
  };

  try {
    const response = await fetch(`${DEEPSEEK_BASE_URL}/chat/completions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${DEEPSEEK_API_KEY}`
      },
      body: JSON.stringify(requestData)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('DeepSeek API调用失败:', error);
    throw error;
  }
};

// 流式调用DeepSeek API (用于实时显示回复)
export const chatWithDeepSeekStream = async (
  messages: ChatMessage[],
  onMessage: (content: string) => void,
  onComplete: () => void,
  onError: (error: Error) => void
) => {
  if (!DEEPSEEK_API_KEY) {
    onError(new Error('Missing VUE_APP_DEEPSEEK_API_KEY'));
    return;
  }

  const requestData: ChatCompletionRequest = {
    model: 'deepseek-chat',
    messages: messages,
    temperature: 0.7,
    max_tokens: 2000,
    stream: true
  };

  try {
    const response = await fetch(`${DEEPSEEK_BASE_URL}/chat/completions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${DEEPSEEK_API_KEY}`
      },
      body: JSON.stringify(requestData)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const reader = response.body?.getReader();
    if (!reader) {
      throw new Error('无法获取响应流');
    }

    const decoder = new TextDecoder();
    let buffer = '';

    while (true) {
      const { done, value } = await reader.read();

      if (done) {
        onComplete();
        break;
      }

      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split('\n');
      buffer = lines.pop() || '';

      for (const line of lines) {
        const trimmedLine = line.trim();
        if (trimmedLine.startsWith('data: ')) {
          const dataStr = trimmedLine.slice(6);

          if (dataStr === '[DONE]') {
            onComplete();
            return;
          }

          try {
            const data = JSON.parse(dataStr);
            const content = data.choices?.[0]?.delta?.content;
            if (content) {
              onMessage(content);
            }
          } catch (e) {
            // 忽略解析错误
          }
        }
      }
    }
  } catch (error) {
    console.error('DeepSeek流式API调用失败:', error);
    onError(error as Error);
  }
};
