package com.medical.service.impl;

import com.medical.entity.mysql.AiQaHistory;
import com.medical.repository.mysql.AiQaHistoryRepository;
import com.medical.service.AiQaService;
import com.medical.service.DeepseekService;
import com.medical.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI问答服务实现类
 */
@Slf4j
@Service
public class AiQaServiceImpl implements AiQaService {
    
    @Autowired
    private DeepseekService deepseekService;
    
    @Autowired
    private AiQaHistoryRepository aiQaHistoryRepository;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    @Transactional
    public String chat(Long userId, String question, List<String> history) {
        // 检查缓存
        String cacheKey = "ai:qa:" + question.hashCode();
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            String answer = (String) cached;
            // 保存历史记录
            saveHistory(userId, question, answer);
            return answer;
        }
        
        try {
            // 构建提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("你是一个专业的健康咨询助手，请根据用户的问题提供专业、准确、易懂的健康建议。");
            prompt.append("\n\n用户问题：").append(question);
            
            if (history != null && !history.isEmpty()) {
                prompt.append("\n\n对话历史：");
                for (int i = 0; i < history.size(); i++) {
                    prompt.append("\n").append(history.get(i));
                }
            }
            
            // 调用DeepSeek API
            String answer = deepseekService.generateText(prompt.toString());
            
            // 缓存24小时
            redisUtil.set(cacheKey, answer, 24, java.util.concurrent.TimeUnit.HOURS);
            
            // 保存历史记录
            saveHistory(userId, question, answer);
            
            return answer;
            
        } catch (Exception e) {
            log.error("AI问答失败", e);
            throw new RuntimeException("AI问答失败：" + e.getMessage());
        }
    }
    
    @Override
    public List<AiQaHistory> getHistory(Long userId) {
        return aiQaHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    private void saveHistory(Long userId, String question, String answer) {
        AiQaHistory history = new AiQaHistory();
        history.setUserId(userId);
        history.setQuestion(question);
        history.setAnswer(answer);
        aiQaHistoryRepository.save(history);
    }
}
