package com.medical.service;

import com.medical.vo.GraphData;

import java.util.List;

/**
 * 知识图谱服务接口
 */
public interface KnowledgeGraphService {
    
    /**
     * 获取疾病相关的知识图谱数据
     */
    GraphData getDiseaseGraph(String diseaseName, int depth);
    
    /**
     * 搜索疾病
     */
    List<Object> searchDiseases(String keyword);
    
    /**
     * 获取所有疾病列表
     */
    List<Object> getAllDiseases();

    /**
     * 获取全量图谱（用于页面初始展示）
     */
    GraphData getFullGraph(int depth, int limit);
}
