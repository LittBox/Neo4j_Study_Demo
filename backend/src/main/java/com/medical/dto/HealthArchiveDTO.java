package com.medical.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 健康档案DTO
 */
@Data
public class HealthArchiveDTO {
    
    private LocalDate birthDate;
    
    private String gender;
    
    private Double height;
    
    private Double weight;
    
    private String bloodType;
    
    private String medicalHistory;
    
    private String allergyHistory;
    
    private String familyHistory;
}
