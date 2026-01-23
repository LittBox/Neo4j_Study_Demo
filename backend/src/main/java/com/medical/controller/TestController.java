package com.medical.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test")
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private Neo4jClient neo4jClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${deepseek.api.key}")
    private String apiKey;

    @GetMapping("/neo4j")
    public ResponseEntity<Map<String, Object>> testNeo4j() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String cypher = "MATCH (n) RETURN count(n) AS count LIMIT 1";
            Long count = neo4jClient.query(cypher)
                .fetchAs(Long.class)
                .mappedBy((typeSystem, record) -> record.get("count").asLong())
                .one()
                .orElse(0L);
            
            result.put("success", true);
            result.put("message", "Neo4j 连接正常");
            result.put("nodeCount", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Neo4j 连接失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/apikey")
    public ResponseEntity<Map<String, Object>> testApiKey() {
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "API Key 未配置");
        } else {
            result.put("success", true);
            result.put("message", "API Key 已配置");
            result.put("keyLength", apiKey.length());
            result.put("keyPreview", apiKey.length() > 8 ? 
                apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4) : "***");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/deepseek")
    public ResponseEntity<Map<String, Object>> testDeepSeek() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = "https://api.deepseek.com/chat/completions";
            String requestBody = """
                {
                    "model": "deepseek-chat",
                    "messages": [
                        {"role": "user", "content": "Hello"}
                    ],
                    "max_tokens": 10
                }
                """;
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);
            
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(requestBody, headers);
            
            String response = restTemplate.postForObject(url, entity, String.class);
            
            result.put("success", true);
            result.put("message", "DeepSeek API 连接正常");
            result.put("response", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "DeepSeek API 连接失败");
            result.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}