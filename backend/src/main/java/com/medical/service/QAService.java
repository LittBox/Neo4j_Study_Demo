package com.medical.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class QAService {
    
    private static final Logger logger = LoggerFactory.getLogger(QAService.class);
    
    @Autowired
    private Neo4jClient neo4jClient;

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${deepseek.api.key}")
    private String deepseekApiKey;
    
    @Value("${deepseek.api.model:deepseek-chat}")
    private String deepseekModel;
    
    @Value("${deepseek.api.baseUrl:https://api.deepseek.com/}")
    private String deepseekBaseUrl;

    public String queryKnowledgeGraph(String userQuestion) {
        try {
            logger.debug("查询知识图谱，用户问题：{}", userQuestion);
            
            // 暂时使用固定查询，后续可以优化为动态查询
            String cypher = "MATCH (d:Disease)-[r:HAS_SYMPTOM]->(s:Symptom) RETURN s.name AS 症状 LIMIT 5";
            
            List<String> results = neo4jClient.query(cypher)
                .fetchAs(String.class)
                .mappedBy((typeSystem, record) -> {
                    try {
                        return record.get("症状").asString();
                    } catch (Exception e) {
                        logger.warn("解析结果失败: {}", e.getMessage());
                        return "";
                    }
                })
                .all()
                .stream()
                .filter(str -> str != null && !str.trim().isEmpty())
                .collect(Collectors.toList());
            
            String kgInfo = String.join("、", results);
            logger.debug("知识图谱查询结果：{}", kgInfo);
            return kgInfo;
        } catch (Exception e) {
            logger.error("查询知识图谱失败", e);
            return "";
        }
    }

    public String generateAnswerWithKG(String userQuestion) {
        logger.info("=== 开始处理用户问题 ===");
        logger.info("用户问题：{}", userQuestion);
        
        // 步骤1：从知识图谱查信息
        String kgInfo = queryKnowledgeGraph(userQuestion);
        if (kgInfo == null || kgInfo.trim().isEmpty()) {
            kgInfo = "暂未从知识图谱获取到相关信息。";
            logger.info("知识图谱未查询到信息");
        } else {
            logger.info("知识图谱信息：{}", kgInfo);
        }
        
        // 步骤2：构造大模型请求
        String prompt = String.format(
            "请基于以下知识回答问题：\n【知识图谱信息】%s\n【用户问题】%s\n【要求】答案必须基于上述知识，语言简洁准确。",
            kgInfo, userQuestion
        );
        
        logger.debug("构造的Prompt：{}", prompt);
        
        // 步骤3：检查API Key
        if (deepseekApiKey == null || deepseekApiKey.trim().isEmpty()) {
            logger.error("DeepSeek API Key 未配置");
            return "系统错误：未配置DeepSeek API Key，请检查配置文件";
        }
        
        String apiKey = deepseekApiKey.trim();
        logger.info("API Key长度：{}，前10位：{}", 
            apiKey.length(), 
            apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : apiKey);
        
        // 步骤4：准备请求
        String apiUrl = deepseekBaseUrl + (deepseekBaseUrl.endsWith("/") ? "" : "/") + "chat/completions";
        logger.info("API URL：{}", apiUrl);
        logger.info("模型：{}", deepseekModel);
        
        DeepseekRequest request = new DeepseekRequest();
        request.setModel(deepseekModel);
        request.setMessages(Arrays.asList(
            new DeepseekMessage("user", prompt)
        ));
        request.setTemperature(0.7);
        request.setMax_tokens(1000);
        
        // 记录请求体（不记录完整的API Key）
        logger.debug("请求体：{}", request.toString().replace(apiKey, "***"));
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/json");
        
        HttpEntity<DeepseekRequest> entity = new HttpEntity<>(request, headers);
        
        // 步骤5：发送请求
        try {
            logger.info("正在调用 DeepSeek API...");
            
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(
                apiUrl,
                entity,
                String.class
            );
            
            HttpStatusCode statusCode = rawResponse.getStatusCode();
            String responseBody = rawResponse.getBody();
            
            logger.info("API 响应状态码：{}", statusCode);
            logger.debug("API 原始响应：{}", responseBody);
            
            if (statusCode.is2xxSuccessful() && responseBody != null) {
                // 简单解析响应
                if (responseBody.contains("\"choices\"")) {
                    // 提取答案（简化处理）
                    int start = responseBody.indexOf("\"content\":\"") + 11;
                    int end = responseBody.indexOf("\"", start);
                    if (start > 10 && end > start) {
                        String answer = responseBody.substring(start, end)
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"");
                        logger.info("成功获取大模型答案");
                        logger.debug("完整答案：{}", answer);
                        return answer;
                    }
                }
                logger.error("响应格式异常：{}", responseBody);
                return "大模型响应格式异常";
            } else if (statusCode.value() == 401) {
                logger.error("API Key 无效或未授权，状态码：{}", statusCode);
                logger.error("响应内容：{}", responseBody);
                return "DeepSeek API 认证失败：请检查API Key是否正确";
            } else {
                logger.error("API 调用失败，状态码：{}，响应：{}", statusCode, responseBody);
                return "大模型调用失败，状态码：" + statusCode;
            }
            
        } catch (Exception e) {
            logger.error("调用 DeepSeek API 异常", e);
            return "调用大模型API失败：" + e.getMessage();
        } finally {
            logger.info("=== 处理完成 ===");
        }
    }

    // 辅助类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeepseekRequest {
        private String model;
        private List<DeepseekMessage> messages;
        private double temperature;
        private int max_tokens;
        private boolean stream = false;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeepseekMessage {
        private String role;
        private String content;
   
    }
}