# 医疗问答系统框架说明

## 项目概述

这是一个基于知识图谱的医疗问答AI辅助回答助手，采用前后端分离架构：
- **前端**：Vue 3 + Vite
- **后端**：Spring Boot 3 + Neo4j

## 项目结构

```
KnoledgeMap/
├── backend/                    # Spring Boot后端
│   ├── src/main/java/com/medical/
│   │   ├── controller/        # API控制器
│   │   │   └── QaController.java
│   │   ├── service/           # 业务逻辑层
│   │   │   └── QaService.java
│   │   ├── repository/        # 数据访问层
│   │   │   ├── QuestionRepository.java
│   │   │   └── MedicalEntityRepository.java
│   │   ├── model/             # 实体模型
│   │   │   ├── Question.java
│   │   │   ├── Answer.java
│   │   │   └── MedicalEntity.java
│   │   ├── dto/               # 数据传输对象
│   │   │   ├── QuestionRequest.java
│   │   │   └── QuestionResponse.java
│   │   └── MedicalQaApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
└── frontend/                   # Vue前端
    ├── src/
    │   ├── components/
    │   │   ├── QuestionArea.vue      # 提问区组件
    │   │   └── AnswerArea.vue        # 回答区组件
    │   ├── App.vue                   # 根组件
    │   └── main.js
    ├── public/
    ├── index.html
    ├── package.json
    └── vite.config.js
```

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
   修改 backend/src/main/resources/application.properties
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

**开发团队** | **2025年**
