package com.medical.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 风险因素节点
 */
@Data
@Node("RiskFactor")
public class RiskFactor {
    
    @Id
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("type")
    private String type; // 生活习惯/遗传/环境
}
