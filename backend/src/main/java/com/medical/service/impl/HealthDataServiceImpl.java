package com.medical.service.impl;

import com.medical.entity.mysql.HealthArchive;
import com.medical.entity.mysql.HealthData;
import com.medical.entity.mysql.MedicineRecord;
import com.medical.repository.mysql.HealthArchiveRepository;
import com.medical.repository.mysql.HealthDataRepository;
import com.medical.repository.mysql.MedicineRecordRepository;
import com.medical.service.HealthDataService;
import com.medical.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康数据服务实现类
 */
@Slf4j
@Service
public class HealthDataServiceImpl implements HealthDataService {
    
    @Autowired
    private HealthArchiveRepository healthArchiveRepository;
    
    @Autowired
    private HealthDataRepository healthDataRepository;
    
    @Autowired
    private MedicineRecordRepository medicineRecordRepository;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public HealthArchive getOrCreateHealthArchive(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        return healthArchiveRepository.findByUserId(userId)
                .orElseGet(() -> {
                    HealthArchive archive = new HealthArchive();
                    archive.setUserId(userId);
                    return healthArchiveRepository.save(archive);
                });
    }
    
    @Override
    @Transactional
    public HealthArchive updateHealthArchive(Long userId, HealthArchive archive) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        HealthArchive existingArchive = getOrCreateHealthArchive(userId);
        
        // 确保userId不会被覆盖
        existingArchive.setUserId(userId);
        
        // 更新字段
        if (archive.getBirthDate() != null) {
            existingArchive.setBirthDate(archive.getBirthDate());
        }
        if (archive.getGender() != null && !archive.getGender().isEmpty()) {
            existingArchive.setGender(archive.getGender());
        }
        if (archive.getHeight() != null) {
            existingArchive.setHeight(archive.getHeight());
        }
        if (archive.getWeight() != null) {
            existingArchive.setWeight(archive.getWeight());
        }
        if (archive.getBloodType() != null && !archive.getBloodType().isEmpty()) {
            existingArchive.setBloodType(archive.getBloodType());
        }
        if (archive.getMedicalHistory() != null) {
            existingArchive.setMedicalHistory(archive.getMedicalHistory());
        }
        if (archive.getAllergyHistory() != null) {
            existingArchive.setAllergyHistory(archive.getAllergyHistory());
        }
        if (archive.getFamilyHistory() != null) {
            existingArchive.setFamilyHistory(archive.getFamilyHistory());
        }
        
        // 再次确保userId被设置
        existingArchive.setUserId(userId);
        return healthArchiveRepository.save(existingArchive);
    }
    
    @Override
    @Transactional
    public HealthData saveHealthData(Long userId, HealthData healthData) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 强制设置userId，确保不为null
        healthData.setUserId(userId);
        
        if (healthData.getRecordTime() == null) {
            healthData.setRecordTime(LocalDateTime.now());
        }
        
        // 验证必要字段
        if (healthData.getDataType() == null || healthData.getDataType().isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        
        // 保存前再次确保userId被设置
        healthData.setUserId(userId);
        
        HealthData saved = healthDataRepository.save(healthData);
        
        // 清除缓存
        String cacheKey = "health:dashboard:" + userId;
        redisUtil.delete(cacheKey);
        
        return saved;
    }
    
    @Override
    public List<HealthData> getHealthDataList(Long userId, String dataType) {
        if (dataType != null && !dataType.isEmpty()) {
            return healthDataRepository.findByUserIdAndDataTypeOrderByRecordTimeDesc(userId, dataType);
        }
        return healthDataRepository.findByUserIdOrderByRecordTimeDesc(userId);
    }
    
    @Override
    public Map<String, Object> getHealthDashboard(Long userId) {
        // 尝试从缓存获取
        String cacheKey = "health:dashboard:" + userId;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (Map<String, Object>) cached;
        }
        
        // 从数据库查询
        Map<String, Object> dashboard = new HashMap<>();
        
        // 获取健康档案
        HealthArchive archive = getOrCreateHealthArchive(userId);
        dashboard.put("archive", archive);
        
        // 获取最近的健康数据
        List<HealthData> recentData = healthDataRepository.findByUserIdOrderByRecordTimeDesc(userId);
        dashboard.put("recentData", recentData.size() > 10 ? recentData.subList(0, 10) : recentData);
        
        // 统计各类型数据
        Map<String, Long> dataTypeCount = new HashMap<>();
        for (HealthData data : recentData) {
            dataTypeCount.put(data.getDataType(), 
                dataTypeCount.getOrDefault(data.getDataType(), 0L) + 1);
        }
        dashboard.put("dataTypeCount", dataTypeCount);
        
        // 缓存1小时
        redisUtil.set(cacheKey, dashboard, 1, java.util.concurrent.TimeUnit.HOURS);
        
        return dashboard;
    }
    
    @Override
    @Transactional
    public MedicineRecord saveMedicineRecord(Long userId, MedicineRecord record) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        record.setUserId(userId);
        return medicineRecordRepository.save(record);
    }
    
    @Override
    public List<MedicineRecord> getMedicineRecordList(Long userId) {
        return medicineRecordRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
}
