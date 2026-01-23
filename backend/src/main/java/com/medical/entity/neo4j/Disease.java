package com.medical.entity.neo4j;
import lombok.Data;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;


@Data
@Node("Disease") // 对应节点标签 Disease
public class Disease {
    @Id // 主键
    private Long id;

    @Property("name") // 节点属性
    private String name;

    @Property("type")
    private String type;

    @Property("intro")
    private String intro;

}