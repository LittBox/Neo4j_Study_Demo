package com.medical.controller;

import com.medical.common.Result;
import com.medical.service.KnowledgeGraphService;
import com.medical.vo.GraphData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "*")
public class KnowledgeGraphController {
    
    @Autowired
    private KnowledgeGraphService knowledgeGraphService;
    
    /**
     * 获取疾病知识图谱
     */
    @GetMapping("/disease")
    public Result<GraphData> getDiseaseGraph(@RequestParam String diseaseName,
                                             @RequestParam(defaultValue = "2") int depth) {
        GraphData graphData = knowledgeGraphService.getDiseaseGraph(diseaseName, depth);
        return Result.success(graphData);
    }
    
    /**
     * 搜索疾病
     */
    @GetMapping("/search")
    public Result<List<Object>> searchDiseases(@RequestParam String keyword) {
        List<Object> diseases = knowledgeGraphService.searchDiseases(keyword);
        return Result.success(diseases);
    }
    
    /**
     * 获取所有疾病列表
     */
    @GetMapping("/diseases")
    public Result<List<Object>> getAllDiseases() {
        List<Object> diseases = knowledgeGraphService.getAllDiseases();
        return Result.success(diseases);
    }
}
