package com.medical.entity.mysql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康档案实体类
 */
@Data
@Entity
@Table(name = "health_archives")
public class HealthArchive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(length = 10)
    private String gender; // 男/女
    
    @Column(name = "height")
    private Double height; // 身高(cm)
    
    @Column(name = "weight")
    private Double weight; // 体重(kg)
    
    @Column(name = "blood_type", length = 10)
    private String bloodType; // 血型
    
    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory; // 既往病史
    
    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory; // 过敏史
    
    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory; // 家族病史
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
