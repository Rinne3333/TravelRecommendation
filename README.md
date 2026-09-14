# TravelRecommendation 模块一自动化测试

本工程直接复制并编译 `vue3_tourism_backend` 的真实后端源码，针对用户端与管理员端的核心业务规则执行 JUnit 5 单元测试。测试不启动 Spring 容器，不连接 MySQL、Redis、Elasticsearch，也不读取任何 `application-*.yml`。

## 测试范围

- 用户与权限：角色判断、登录视图对象转换、注册接口参数契约。
- 攻略：新增必填项、标题 80 字边界、正文 8192 字边界、编辑规则、模糊查询与排序安全。
- 游客留言与公告：弹幕内容边界、用户及管理员标识校验、精选筛选条件。
- 景点业务：景点、路线、评分、门票、订单、景点收藏的新增与编辑校验。
- 管理功能：公告、景点、路线、评分、门票、订单等管理端共用的服务校验规则。

明确不包含 `UserAiMessageController`、`UserAiMessageServiceImpl`、AI 对话管理及大模型调用测试，这些内容留给模块二。

## 环境

- JDK 8 或更高版本
- Windows、macOS 或 Linux
- 首次执行需要访问 Maven Central；项目附带 Maven Wrapper，无需单独安装 Maven

## 一键执行

Windows IDEA 终端或 PowerShell：

```powershell
.\run-tests.ps1
```

也可以直接执行：

```powershell
.\mvnw.cmd -Dtest="com.xiaobaitiao.springbootinit.module1.*Test" test
```

macOS / Linux：

```bash
./mvnw -Dtest='com.xiaobaitiao.springbootinit.module1.*Test' test
```

## 结果说明

测试类中以 `defect` 开头的方法属于缺陷复现用例。它们断言的是当前源码已经存在的异常行为，因此在缺陷未修复时也会通过，并在缺陷报告中单独标记为“已复现、待修复”。其余测试断言业务规则的预期行为。

Maven 的 XML 结果位于 `target/surefire-reports/`，终端会输出测试总数、失败数与错误数。
