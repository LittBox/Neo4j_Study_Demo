package com.medical.service;

import com.medical.entity.mysql.AiQaHistory;

import java.util.List;

/**
 * AI问答服务接口
 */
public interface AiQaService {
    
    /**
     * 问答
     */
    String chat(Long userId, String question, List<String> history);
    
    /**
     * 获取问答历史
     */
    List<AiQaHistory> getHistory(Long userId);
}
