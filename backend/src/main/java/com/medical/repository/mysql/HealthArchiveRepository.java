package com.medical.repository.mysql;

import com.medical.entity.mysql.HealthArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 健康档案Repository
 */
@Repository
public interface HealthArchiveRepository extends JpaRepository<HealthArchive, Long> {
    
    Optional<HealthArchive> findByUserId(Long userId);
}
