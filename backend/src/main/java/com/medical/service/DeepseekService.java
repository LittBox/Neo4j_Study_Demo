package com.medical.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

@Service
public class DeepseekService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.model:deepseek-chat}")
    private String model;

    @Value("${deepseek.api.baseUrl:https://api.deepseek.com/}")
    private String baseUrl;

    public String generateText(String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }

        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", model);

        ArrayNode messages = mapper.createArrayNode();
        ObjectNode userMessage = mapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        payload.set("messages", messages);

        String url = baseUrl;
        if (!url.endsWith("/")) url += "/";
        url += "chat/completions";

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)));

        if (apiKey != null && !apiKey.isEmpty()) {
            reqBuilder.header("Authorization", "Bearer " + apiKey);
        }

        HttpRequest request = reqBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectNode apiResponse = (ObjectNode) mapper.readTree(response.body());

            if (apiResponse.has("choices") && apiResponse.get("choices").isArray()) {
                var choices = apiResponse.get("choices");
                if (choices.size() > 0) {
                    var choice = choices.get(0);
                    if (choice.has("message") && choice.get("message").has("content")) {
                        return choice.get("message").get("content").asText();
                    } else if (choice.has("text")) {
                        return choice.get("text").asText();
                    }
                }
            }

            if (apiResponse.has("result")) {
                return apiResponse.get("result").asText();
            }

            throw new RuntimeException("Failed to extract content from Deepseek response");

        } else {
            throw new RuntimeException("Deepseek API error: " + response.statusCode() + " - " + response.body());
        }
    }
}
