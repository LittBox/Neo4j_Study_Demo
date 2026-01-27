package com.medical.controller;

import com.medical.common.Result;
import com.medical.dto.HealthArchiveDTO;
import com.medical.dto.HealthDataDTO;
import com.medical.entity.mysql.HealthArchive;
import com.medical.entity.mysql.HealthData;
import com.medical.entity.mysql.MedicineRecord;
import com.medical.service.HealthDataService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 健康数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthDataController {
    
    @Autowired
    private HealthDataService healthDataService;
    
    /**
     * 获取健康档案
     */
    @GetMapping("/archive")
    public Result<HealthArchive> getHealthArchive(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }
            HealthArchive archive = healthDataService.getOrCreateHealthArchive(userId);
            return Result.success(archive);
        } catch (Exception e) {
            log.error("获取健康档案失败", e);
            return Result.error(500, "获取健康档案失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新健康档案
     */
    @PutMapping("/archive")
    public Result<HealthArchive> updateHealthArchive(@RequestBody HealthArchiveDTO dto, 
                                                      HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                log.error("userId为null，request attributes: {}", request.getAttributeNames());
                return Result.error(401, "用户未登录");
            }
            HealthArchive archive = new HealthArchive();
            BeanUtils.copyProperties(dto, archive);
            archive.setUserId(userId);
            HealthArchive updated = healthDataService.updateHealthArchive(userId, archive);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            log.error("更新健康档案失败", e);
            return Result.error(500, "更新健康档案失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存健康数据
     */
    @PostMapping("/data")
    public Result<HealthData> saveHealthData(@RequestBody HealthDataDTO dto, 
                                             HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                log.error("userId为null，无法保存健康数据，URI: {}", request.getRequestURI());
                return Result.error(401, "用户未登录");
            }
            
            HealthData healthData = new HealthData();
            healthData.setDataType(dto.getDataType());
            healthData.setValue1(dto.getValue1());
            healthData.setValue2(dto.getValue2());
            healthData.setUnit(dto.getUnit());
            healthData.setDeviceType(dto.getDeviceType());
            healthData.setRemark(dto.getRemark());
            healthData.setUserId(userId);
            healthData.setRecordTime(dto.getRecordTime() != null ? dto.getRecordTime() : java.time.LocalDateTime.now());
            
            HealthData saved = healthDataService.saveHealthData(userId, healthData);
            return Result.success("保存成功", saved);
        } catch (Exception e) {
            log.error("保存健康数据失败", e);
            return Result.error(500, "保存健康数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取健康数据列表
     */
    @GetMapping("/data")
    public Result<List<HealthData>> getHealthDataList(@RequestParam(required = false) String dataType,
                                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        List<HealthData> list = healthDataService.getHealthDataList(userId, dataType);
        return Result.success(list);
    }
    
    /**
     * 获取健康看板
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getHealthDashboard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        Map<String, Object> dashboard = healthDataService.getHealthDashboard(userId);
        return Result.success(dashboard);
    }
    
    /**
     * 保存用药记录
     */
    @PostMapping("/medicine")
    public Result<MedicineRecord> saveMedicineRecord(@RequestBody MedicineRecord record,
                                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        MedicineRecord saved = healthDataService.saveMedicineRecord(userId, record);
        return Result.success("保存成功", saved);
    }
    
    /**
     * 获取用药记录列表
     */
    @GetMapping("/medicine")
    public Result<List<MedicineRecord>> getMedicineRecordList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        List<MedicineRecord> list = healthDataService.getMedicineRecordList(userId);
        return Result.success(list);
    }
}
