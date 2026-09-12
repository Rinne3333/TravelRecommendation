# TravelRecommendation AI 质量自动化测试

本工程对应模块二方案一，测试对象是 TravelRecommendation 的 AI 旅游助手及其调用边界。测试重点为鲁棒性、公平性与安全性，同时复现并验证两个来自现有项目的缺陷：浏览器端密钥暴露、SSE 末尾残片丢失。

测试采用离线脚本模型，不访问 DeepSeek 或讯飞星火，不读取任何真实密钥。脚本模型返回固定结果，适合持续集成和重复回归；它验证的是 AI 应用的输入治理、输出质量门禁、异常降级和流式协议处理，不替代真实模型的人工语义评测。

## 一键执行

Windows PowerShell：

```powershell
.\mvnw.cmd test
```

macOS / Linux：

```bash
./mvnw test
```

成功标准：Maven 输出 `BUILD SUCCESS`，测试汇总为 `Tests run: 36, Failures: 0, Errors: 0, Skipped: 0`。

## 环境

- JDK 8 或更高版本
- 首次执行需要访问 Maven Central；工程已附 Maven Wrapper，无需预装 Maven
- 不需要数据库、Redis、浏览器或 AI 服务账号

## 目录说明

```text
src/main/java/com/xiaobaitiao/aiquality/
  TravelAiGuard.java                  输入与输出质量门禁
  TravelAiService.java                AI 调用、降级与门禁编排
  SseEventDecoder.java                可处理分块及末尾残片的 SSE 解码器
  FrontendSecretExposureDetector.java 前端密钥暴露静态检测器

src/test/java/com/xiaobaitiao/aiquality/
  TravelAiGuardTest.java
  TravelAiServiceTest.java
  SseEventDecoderTest.java
  FrontendSecretExposureDetectorTest.java
```

## 与主项目的对应关系

- 前端 AI 调用：`vue3_tourism_frontend/src/api/deepseekController.ts`
- 前端对话页：`vue3_tourism_frontend/src/views/user/AITalk/index.vue`
- 后端星火调用：`vue3_tourism_backend/src/main/java/com/xiaobaitiao/springbootinit/manager/SparkClient.java`

建议生产修复：密钥仅保存在后端环境变量中，前端改调同源后端接口；将 `SseEventDecoder` 的末尾残片处理逻辑迁入实际流式客户端。修复合入后，应再执行本工程并补充真实模型小样本人工评测。
