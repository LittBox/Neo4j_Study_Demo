package com.medical.service.impl;

import com.medical.entity.neo4j.Disease;
import com.medical.repository.DiseaseRepository;
import com.medical.service.KnowledgeGraphService;
import com.medical.util.RedisUtil;
import com.medical.vo.GraphData;
import com.medical.vo.GraphLink;
import com.medical.vo.GraphNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识图谱服务实现类
 */
@Slf4j
@Service
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {
    
    @Autowired
    private DiseaseRepository diseaseRepository;
    
    @Autowired
    private Neo4jClient neo4jClient;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Override
    public GraphData getDiseaseGraph(String diseaseName, int depth) {
        String cacheKey = "graph:disease:" + diseaseName + ":depth:" + depth;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (GraphData) cached;
        }

        GraphData graphData = new GraphData();
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphLink> links = new ArrayList<>();
        Map<Long, GraphNode> nodeMap = new HashMap<>();

        String cypher = "MATCH p=(d:Disease {name:$diseaseName})-[*1.." + depth + "]-(n) " +
                "RETURN nodes(p) AS ns, relationships(p) AS rs";

        try {
            Collection<Map<String, Object>> rows = neo4jClient.query(cypher)
                    .bindAll(Map.of("diseaseName", diseaseName))
                    .fetch().all();

            for (Map<String, Object> row : rows) {
                @SuppressWarnings("unchecked")
                List<org.neo4j.driver.types.Node> ns = (List<org.neo4j.driver.types.Node>) row.get("ns");
                @SuppressWarnings("unchecked")
                List<org.neo4j.driver.types.Relationship> rs = (List<org.neo4j.driver.types.Relationship>) row.get("rs");

                for (org.neo4j.driver.types.Node n : ns) {
                    long id = n.id();
                    if (nodeMap.containsKey(id)) continue;
                    GraphNode gn = new GraphNode();
                    gn.setId(String.valueOf(id));
                    gn.setName(n.get("name").asString(""));
                    String category = n.labels().stream().findFirst().orElse("other");
                    gn.setCategory(category.toLowerCase());
                    switch (category) {
                        case "Disease": gn.setSymbolSize(30); break;
                        case "Symptom": gn.setSymbolSize(20); break;
                        case "RiskFactor": gn.setSymbolSize(20); break;
                        case "Treatment": gn.setSymbolSize(25); break;
                        case "Drug": gn.setSymbolSize(15); break;
                        default: gn.setSymbolSize(18);
                    }
                    nodeMap.put(id, gn);
                }

                for (org.neo4j.driver.types.Relationship r : rs) {
                    GraphLink gl = new GraphLink();
                    gl.setSource(String.valueOf(r.startNodeId()));
                    gl.setTarget(String.valueOf(r.endNodeId()));
                    gl.setName(r.type());
                    gl.setValue(1);
                    links.add(gl);
                }
            }

            nodes.addAll(nodeMap.values());
            graphData.setNodes(nodes);
            graphData.setLinks(links);
            redisUtil.set(cacheKey, graphData, 24, java.util.concurrent.TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("查询知识图谱失败", e);
            throw new RuntimeException("查询知识图谱失败：" + e.getMessage());
        }

        return graphData;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> searchDiseases(String keyword) {
        String cacheKey = "graph:search:" + keyword;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (List<Object>) cached;
        }
        
        String cypher = "MATCH (d:Disease) WHERE d.name CONTAINS $keyword RETURN d LIMIT 20";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("keyword", keyword);
        Collection<Map<String, Object>> resultsCollection = neo4jClient.query(cypher)
                .bindAll(parameters)
                .fetch()
                .all();
        List<Map<String, Object>> results = new ArrayList<>(resultsCollection);
        
        List<Object> diseases = results.stream()
                .map(row -> row.get("d"))
                .collect(Collectors.toList());
        
        // 缓存1小时
        redisUtil.set(cacheKey, diseases, 1, java.util.concurrent.TimeUnit.HOURS);
        
        return diseases;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAllDiseases() {
        String cacheKey = "graph:all:diseases";
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (List<Object>) cached;
        }
        
        List<Disease> diseases = diseaseRepository.findAll();
        List<Object> result = new ArrayList<>(diseases);
        
        // 缓存1小时
        redisUtil.set(cacheKey, result, 1, java.util.concurrent.TimeUnit.HOURS);
        
        return result;
    }
}
