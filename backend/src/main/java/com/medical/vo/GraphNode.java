package com.medical.vo;

import lombok.Data;

/**
 * 图谱节点VO
 */
@Data
public class GraphNode {
    private String id;
    private String name;
    private String category; // 节点类型：disease, symptom, riskFactor, treatment, drug
    private Integer symbolSize;
    private Object value;
}
