package com.medical.repository.mysql;

import com.medical.entity.mysql.MedicineRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用药记录Repository
 */
@Repository
public interface MedicineRecordRepository extends JpaRepository<MedicineRecord, Long> {
    
    List<MedicineRecord> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    List<MedicineRecord> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status);
}
