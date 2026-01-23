package com.medical;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.medical.entity.neo4j.Disease;
import com.medical.repository.DiseaseRepository;

@SpringBootTest // 启动 Spring 上下文
public class Neo4jSingleDataTest {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Test
    public void testCreateDisease() {
        // 构建疾病数据
        Disease disease = new Disease();
        disease.setId(2L);
        disease.setName("冠心病");
        disease.setType("心血管疾病");
        disease.setIntro("冠状动脉粥样硬化导致的心脏病");

        // 保存到 Neo4j
        diseaseRepository.save(disease);
        System.out.println("单条疾病数据创建成功！");
    }
}