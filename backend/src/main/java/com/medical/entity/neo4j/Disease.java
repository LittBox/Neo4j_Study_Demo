package com.medical.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 疾病节点
 */
@Data
@Node("Disease")
public class Disease {
    
    @Id
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("type")
    private String type;
    
    @Property("intro")
    private String intro;
    
    @Property("incidence")
    private String incidence; // 发病率
}