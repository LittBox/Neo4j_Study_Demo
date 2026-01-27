package com.medical.entity.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 药物节点
 */
@Data
@Node("Drug")
public class Drug {
    
    @Id
    private Long id;
    
    @Property("name")
    private String name;
    
    @Property("efficacy")
    private String efficacy; // 功效
    
    @Property("usage")
    private String usage; // 用法用量
    
    @Property("contraindication")
    private String contraindication; // 禁忌
}
