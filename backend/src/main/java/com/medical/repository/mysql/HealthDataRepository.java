package com.medical.repository.mysql;

import com.medical.entity.mysql.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康数据Repository
 */
@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
    
    List<HealthData> findByUserIdOrderByRecordTimeDesc(Long userId);
    
    List<HealthData> findByUserIdAndDataTypeOrderByRecordTimeDesc(Long userId, String dataType);
    
    @Query("SELECT h FROM HealthData h WHERE h.userId = :userId AND h.recordTime BETWEEN :startTime AND :endTime ORDER BY h.recordTime DESC")
    List<HealthData> findByUserIdAndRecordTimeBetween(@Param("userId") Long userId, 
                                                      @Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime);
}
