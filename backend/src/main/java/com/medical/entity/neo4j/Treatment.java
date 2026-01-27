package com.medical.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 治疗方案节点
 */
@Data
@Node("Treatment")
public class Treatment {
    
    @Id
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("type")
    private String type; // 药物/手术/护理
    
    @Property("description")
    private String description;
}
