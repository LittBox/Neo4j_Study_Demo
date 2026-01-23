package com.medical.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class QAService {
    
    @Autowired
    private Neo4jClient neo4jClient;

    

    // 2. 解析问题，生成Cypher查询（示例：用户问"糖尿病的症状有哪些"）
    public String queryKnowledgeGraph(String userQuestion) {
        // 简单示例：假设问题是"糖尿病的症状"，构造Cypher查"糖尿病"的"症状"关系
        String cypher = "MATCH (d:疾病 {名称: '糖尿病'})-[r:症状]->(s:症状) RETURN s.名称 AS 症状";
        
        // 执行查询，拼接结果
        return neo4jClient.query(cypher)
            .fetchAs(String.class)
            .mappedBy((typeSystem, record) -> record.get("症状").asString())
            .all()  // 改为 all() 而不是 list()
            .stream()
            .collect(Collectors.joining("、")); // 结果示例："多饮、多食、多尿、体重下降"
    }

    // 1. 注入RestTemplate（或用Deepseek SDK）
    @Autowired
    private RestTemplate restTemplate;

    // 2. 调用大模型，结合知识图谱信息
    public String generateAnswerWithKG(String userQuestion) {
        // 步骤1：从知识图谱查信息
        String kgInfo = queryKnowledgeGraph(userQuestion);
        if (kgInfo == null || kgInfo.isEmpty()) {
            kgInfo = "暂未从知识图谱获取到相关信息，请基于你的知识回答。";
        }

        // 构造大模型的Prompt（核心：把知识图谱信息注入）
        String prompt = String.format(
            "请基于以下知识回答问题：%s\n用户问题：%s\n要求：答案必须基于上述知识，语言简洁准确。",
            kgInfo, userQuestion
        );

        // 调用Deepseek API
        DeepseekRequest request = new DeepseekRequest();
        request.setModel("deepseek-chat");
        request.setMessages(Arrays.asList(
            new DeepseekMessage("user", prompt)
        ));

        // 发送请求（需配置API Key）
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + System.getenv("DEEPSEEK_API_KEY"));
        HttpEntity<DeepseekRequest> entity = new HttpEntity<>(request, headers);

        try {
            DeepseekResponse response = restTemplate.postForObject(
                "https://api.deepseek.com/chat/completions",
                entity,
                DeepseekResponse.class
            );

            // 返回大模型生成的答案
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "大模型未返回有效答案";
        } catch (Exception e) {
            return "调用大模型API失败: " + e.getMessage();
        }
    }

    // 辅助类：Deepseek请求/响应的POJO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeepseekRequest {
        private String model;
        private List<DeepseekMessage> messages;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeepseekMessage {
        private String role;
        private String content;
    
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeepseekResponse {
        private List<DeepseekChoice> choices;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeepseekChoice {
        private DeepseekMessage message;
    }
}