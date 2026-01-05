# 医疗问答系统（KnowledgeMap）

简洁说明、运行与接口文档（基于当前代码树）

## 项目概述

这是一个前后端分离的医疗问答项目：
- 后端：Spring Boot（Java 21）、Neo4j，用于知识图谱查询与 AI Agent 接口调用（Deepseek/其他）。
- 前端：Vue 3 + Vite，提供问答 UI 并调用后端 `/api/chat` 接口。

## 当前主要文件/目录（摘录）

```
KnoledgeMap/
├── backend/
│   ├── pom.xml
    └── src/main/java/com/medical/
          ├── MedicalQaApplication.java
          ├── controller/
          │   └── AiController.java        # /api/chat
          ├── service/
          │   ├── DeepseekService.java     # 调用 https://api.deepseek.com
          └── resources/
                └── application.yml          # 配置（DEEPSEEK_API_KEY 等）
└── frontend/
      ├── index.html
      ├── package.json
      └── src/
            ├── main.js
            └── views/
                  └── ChatView.vue            # 提问文本框 -> POST /api/chat
```

## 环境与配置

- Java 21、Maven、Node 及 npm
- 在 `backend/src/main/resources/application.yml` 中读取 Deepseek API 配置：

```yaml
deepseek:
   api:
      key: ${DEEPSEEK_API_KEY:}
      model: deepseek-chat
      baseUrl: https://api.deepseek.com/
```

请在运行前设置环境变量（示例）：

Windows PowerShell:
```powershell
$env:DEEPSEEK_API_KEY = "your-api-key-here"
```

Linux / macOS:
```bash
export DEEPSEEK_API_KEY=your-api-key-here
```

## 运行说明

后端：
```bash
cd backend
mvn clean package
mvn spring-boot:run
```

后端默认监听： `http://localhost:8080`

前端：
```bash
cd frontend
npm install
npm run dev
```

前端开发服务器（Vite）通常为 `http://localhost:5173`。

## 后端 API（当前已实现）

POST /api/chat
- 请求体：`{ "prompt": "文本内容" }`
- 返回（成功示例）：

```json
{
   "success": true,
   "content": "Deepseek 或 Agent 返回的文本"
}
```

错误返回包含 `success:false` 和 `error` 字段。

备注：前端 `ChatView.vue` 已将输入的文本作为 `prompt` 发送到该接口。

## 变更历史（简短）

- 已实现 `DeepseekService` 并在 `AiController` 中集成；前端 `ChatView.vue` 发送 `prompt` 字段并显示 `content`。
- 添加并更新了 `.gitignore`，已移除不应提交的 `node_modules`。

---

## 功能描述

### 后端功能

1. **QaController** - API端点
   - `POST /api/qa/ask` - 提交问题并获取答案
   - `GET /api/qa/health` - 健康检查

2. **QaService** - 问答业务逻辑
   - 处理问题的解析和处理
   - 与知识图谱交互
   - 返回答案和置信度

3. **数据模型**
   - `Question` - 问题实体
   - `Answer` - 答案实体
   - `MedicalEntity` - 医学实体（疾病、症状、药物等）

### 前端功能

1. **QuestionArea.vue** - 提问区
   - 文本输入框用于输入问题
   - 提交和清除按钮
   - 使用提示显示

2. **AnswerArea.vue** - 回答区
   - 显示提交的问题
   - 展示AI生成的答案
   - 显示答案的置信度（进度条）
   - 显示时间戳

3. **App.vue** - 主应用
   - 整合提问和回答组件
   - 处理与后端的API通信
   - 管理应用状态

## 快速开始

### 后端启动

1. **配置Neo4j**
   ```
   修改 backend/src/main/resources/application.yml
   更新Neo4j连接参数
   ```

2. **构建和运行**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

后端将在 `http://localhost:8080` 上运行

### 前端启动

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **开发服务**
   ```bash
   npm run dev
   ```

前端将在 `http://localhost:5173` 上运行

## 下一步开发

### 后端优化

1. **集成知识图谱查询**
   - 实现Neo4j Cypher查询
   - 构建医学实体图
   - 实现关系挖掘

2. **NLP处理**
   - 问题分类和抽取
   - 实体识别
   - 语义相似度匹配

3. **答案生成**
   - 从知识图谱检索相关信息
   - 组织和格式化答案
   - 计算置信度

### 前端优化

1. **UI/UX改进**
   - 问题历史记录
   - 相关问题推荐
   - 答案收藏功能

2. **功能扩展**
   - 多语言支持
   - 深色模式
   - 响应式优化

3. **错误处理**
   - 更详细的错误提示
   - 重试机制
   - 离线支持

## 技术栈

- **前端**：Vue 3, Vite, Axios
- **后端**：Spring Boot 3, Spring Data Neo4j, Lombok
- **数据库**：Neo4j
- **构建工具**：Maven (后端), npm (前端)

## API接口

### 提交问题

**请求**
```
POST /api/qa/ask
Content-Type: application/json

{
  "question": "什么是高血压？"
}
```

**响应**
```json
{
  "question": "什么是高血压？",
  "answer": "高血压是指...",
  "confidence": 0.85,
  "timestamp": 1704067200000
}
```

## 注意事项

- 确保Neo4j服务正常运行
- CORS已启用，允许跨域请求
- 前端开发时使用代理转发API请求
- 所有答案仅供参考，不构成医疗建议

---

**开发团队** | **2026年**
