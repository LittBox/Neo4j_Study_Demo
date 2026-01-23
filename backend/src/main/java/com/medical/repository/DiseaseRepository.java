package com.medical.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.medical.entity.neo4j.Disease;

// 继承 Neo4jRepository，泛型：实体类 + 主键类型
public interface DiseaseRepository extends Neo4jRepository<Disease, Long> {
}