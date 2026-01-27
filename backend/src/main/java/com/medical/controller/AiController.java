package com.medical.controller;

import com.medical.common.Result;
import com.medical.entity.mysql.AiQaHistory;
import com.medical.service.AiQaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI问答控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {
    
    @Autowired
    private AiQaService aiQaService;
    
    /**
     * AI问答
     */
    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, Object> body,
                                             HttpServletRequest request) {
        String question = (String) body.get("question");
        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) body.get("history");
        
        if (question == null || question.trim().isEmpty()) {
            return Result.error(400, "问题不能为空");
        }
        
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        String answer = aiQaService.chat(userId, question, history);
        
        Map<String, String> data = new HashMap<>();
        data.put("question", question);
        data.put("answer", answer);
        
        return Result.success("问答成功", data);
    }
    
    /**
     * 获取问答历史
     */
    @GetMapping("/history")
    public Result<List<AiQaHistory>> getHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        List<AiQaHistory> history = aiQaService.getHistory(userId);
        return Result.success(history);
    }
}