package com.medical.entity.mysql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用药记录实体类
 */
@Data
@Entity
@Table(name = "medicine_records")
public class MedicineRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "medicine_name", length = 100, nullable = false)
    private String medicineName; // 药品名称
    
    @Column(name = "dosage", length = 50)
    private String dosage; // 用法用量
    
    @Column(name = "frequency", length = 50)
    private String frequency; // 服用频率
    
    @Column(name = "start_date")
    private LocalDateTime startDate; // 开始日期
    
    @Column(name = "end_date")
    private LocalDateTime endDate; // 结束日期
    
    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose; // 用药目的
    
    @Column(name = "doctor_name", length = 50)
    private String doctorName; // 开药医生
    
    @Column(name = "status", length = 20)
    private String status; // 状态：taking, finished, stopped
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark; // 备注
    
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
