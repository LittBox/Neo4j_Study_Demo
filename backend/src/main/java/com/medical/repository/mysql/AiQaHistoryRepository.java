package com.medical.repository.mysql;

import com.medical.entity.mysql.AiQaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI问答历史Repository
 */
@Repository
public interface AiQaHistoryRepository extends JpaRepository<AiQaHistory, Long> {
    
    List<AiQaHistory> findByUserIdOrderByCreateTimeDesc(Long userId);
}
