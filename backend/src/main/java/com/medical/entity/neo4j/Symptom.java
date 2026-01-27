package com.medical.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 症状节点
 */
@Data
@Node("Symptom")
public class Symptom {
    
    @Id
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("description")
    private String description;
    
    @Property("frequency")
    private String frequency; // 出现频率
}
