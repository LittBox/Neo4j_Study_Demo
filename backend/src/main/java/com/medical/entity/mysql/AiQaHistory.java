package com.medical.entity.mysql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI问答历史实体类
 */
@Data
@Entity
@Table(name = "ai_qa_history")
public class AiQaHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question; // 问题
    
    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer; // 回答
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
