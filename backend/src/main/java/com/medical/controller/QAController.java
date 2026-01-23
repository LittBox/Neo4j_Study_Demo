package com.medical.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical.service.QAService;



@RestController
@RequestMapping("/qa")
public class QAController {
     @Autowired
    private QAService qaService; // 包含上述generateAnswerWithKG方法

    @PostMapping
    public ResponseEntity<Map<String, Object>> answer(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = qaService.generateAnswerWithKG(question);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("answer", answer);
        return ResponseEntity.ok(result);
    }
}
