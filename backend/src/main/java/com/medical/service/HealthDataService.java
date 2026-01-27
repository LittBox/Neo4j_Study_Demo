package com.medical.service;

import com.medical.entity.mysql.HealthArchive;
import com.medical.entity.mysql.HealthData;
import com.medical.entity.mysql.MedicineRecord;

import java.util.List;
import java.util.Map;

/**
 * 健康数据服务接口
 */
public interface HealthDataService {
    
    /**
     * 获取或创建健康档案
     */
    HealthArchive getOrCreateHealthArchive(Long userId);
    
    /**
     * 更新健康档案
     */
    HealthArchive updateHealthArchive(Long userId, HealthArchive archive);
    
    /**
     * 保存健康数据
     */
    HealthData saveHealthData(Long userId, HealthData healthData);
    
    /**
     * 获取健康数据列表
     */
    List<HealthData> getHealthDataList(Long userId, String dataType);
    
    /**
     * 获取健康看板统计数据
     */
    Map<String, Object> getHealthDashboard(Long userId);
    
    /**
     * 保存用药记录
     */
    MedicineRecord saveMedicineRecord(Long userId, MedicineRecord record);
    
    /**
     * 获取用药记录列表
     */
    List<MedicineRecord> getMedicineRecordList(Long userId);
}
