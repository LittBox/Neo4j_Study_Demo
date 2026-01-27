package com.medical.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 健康数据DTO
 */
@Data
public class HealthDataDTO {
    
    private String dataType;
    
    private Double value1;
    
    private Double value2;
    
    private String unit;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime recordTime;
    
    private String deviceType;
    
    private String remark;
}
