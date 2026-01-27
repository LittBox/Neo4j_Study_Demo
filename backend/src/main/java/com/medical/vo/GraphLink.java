package com.medical.vo;

import lombok.Data;

/**
 * 图谱关系VO
 */
@Data
public class GraphLink {
    private String source;
    private String target;
    private String name; // 关系名称
    private Integer value;
}
