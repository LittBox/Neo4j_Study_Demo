# OpenAI 集成说明

## 概述
已将后端改为使用 OpenAI API，实现前端发送 prompt，后端调用 OpenAI 生成文本并返回文本字段。

## 代码变更

### 1. 新增服务类
**文件**: `backend/src/main/java/com/medical/service/OpenAiService.java`
- 创建 `OpenAiService` 类用于处理 OpenAI API 调用
- 方法: `generateText(String prompt)` 返回生成的文本内容

### 2. 更新 Controller
**文件**: `backend/src/main/java/com/medical/controller/AiController.java`
- 移除对 Baidu Qianfan API 的调用
- 注入 `OpenAiService` 依赖
- `/api/chat` 端点现在使用 OpenAI 生成文本

### 3. 配置更新
**文件**: `backend/src/main/resources/application.yml`
```yaml
openai:
  api:
    key: ${OPENAI_API_KEY:}  # 从环境变量获取 API Key
    model: gpt-3.5-turbo      # 使用的模型，可改为 gpt-4 等
    baseUrl: https://api.openai.com/v1/
```

## 使用方法

### 1. 设置 OpenAI API Key
在启动应用前，设置环境变量：

**Windows PowerShell:**
```powershell
$env:OPENAI_API_KEY = "your-api-key-here"
```

**Windows CMD:**
```cmd
set OPENAI_API_KEY=your-api-key-here
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY=your-api-key-here
```

### 2. API 调用

**请求**:
```json
POST /api/chat
Content-Type: application/json

{
  "prompt": "What are the symptoms of diabetes?"
}
```

**成功响应** (200 OK):
```json
{
  "success": true,
  "content": "OpenAI 生成的回答文本..."
}
```

**错误响应** (400/500):
```json
{
  "success": false,
  "error": "错误信息",
  "errorType": "异常类型"
}
```

## 依赖
已在 `pom.xml` 中添加 OpenAI Java 依赖：
```xml
<dependency>
    <groupId>com.openai</groupId>
    <artifactId>openai-java</artifactId>
    <version>4.13.0</version>
</dependency>
```

## 前端集成
前端 `ChatView.vue` 已正确配置：
- 发送 POST 请求到 `/api/chat`
- 在请求体中包含 `prompt` 字段
- 从响应中获取 `content` 字段显示结果

## 自定义配置

### 修改模型
在 `application.yml` 中更改 `openai.api.model`:
```yaml
openai:
  api:
    model: gpt-4  # 或其他可用模型
```

### 调整生成参数
在 `OpenAiService.java` 中修改 payload：
```java
payload.put("max_tokens", 2048);      // 最大生成令牌数
payload.put("temperature", 0.7);      // 创意度 (0-2)
```

## 故障排除

### 问题：API Key 未设置
**解决**: 确保设置了 `OPENAI_API_KEY` 环境变量

### 问题：连接超时
**解决**: 检查网络连接，确保可以访问 api.openai.com

### 问题：API 额度不足
**解决**: 检查 OpenAI 账户的额度和使用情况
