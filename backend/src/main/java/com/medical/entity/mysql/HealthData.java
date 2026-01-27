package com.medical.entity.mysql;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 健康数据追踪实体类
 */
@Data
@Entity
@Table(name = "health_data")
public class HealthData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "data_type", length = 50, nullable = false)
    private String dataType; // 数据类型：blood_pressure, heart_rate, blood_sugar, weight, etc.
    
    @Column(name = "value1")
    private Double value1; // 值1（如收缩压）
    
    @Column(name = "value2")
    private Double value2; // 值2（如舒张压）
    
    @Column(name = "unit", length = 20)
    private String unit; // 单位
    
    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime; // 记录时间
    
    @Column(name = "device_type", length = 50)
    private String deviceType; // 设备类型
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark; // 备注
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (this.recordTime == null) {
            this.recordTime = LocalDateTime.now();
        }
    }
}
