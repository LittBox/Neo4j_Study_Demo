package com.medical;

import com.medical.entity.neo4j.Disease;
import com.medical.entity.neo4j.Symptom;
import com.medical.entity.neo4j.RiskFactor;
import com.medical.entity.neo4j.Treatment;
import com.medical.entity.neo4j.Drug;
import com.medical.repository.DiseaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class KnowledgeGraphDataTest {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    @Test
    public void createKnowledgeGraphData() {
        // 创建疾病
        Disease disease1 = new Disease();
        disease1.setName("高血压");
        disease1.setType("心血管疾病");
        disease1.setIntro("血压持续升高的慢性疾病");
        disease1.setIncidence("高");
        disease1 = diseaseRepository.save(disease1);

        Disease disease2 = new Disease();
        disease2.setName("冠心病");
        disease2.setType("心血管疾病");
        disease2.setIntro("冠状动脉粥样硬化导致的心脏病");
        disease2.setIncidence("中");
        disease2 = diseaseRepository.save(disease2);

        // 创建症状
        String createSymptomCypher = "CREATE (s:Symptom {id: $id, name: $name, description: $desc, frequency: $freq}) RETURN s";
        
        Map<String, Object> symptom1 = new HashMap<>();
        symptom1.put("id", 1L);
        symptom1.put("name", "头痛");
        symptom1.put("desc", "持续性或阵发性头痛");
        symptom1.put("freq", "常见");
        neo4jClient.query(createSymptomCypher).bindAll(symptom1).run();

        Map<String, Object> symptom2 = new HashMap<>();
        symptom2.put("id", 2L);
        symptom2.put("name", "胸闷");
        symptom2.put("desc", "胸部压迫感或不适");
        symptom2.put("freq", "常见");
        neo4jClient.query(createSymptomCypher).bindAll(symptom2).run();

        Map<String, Object> symptom3 = new HashMap<>();
        symptom3.put("id", 3L);
        symptom3.put("name", "心悸");
        symptom3.put("desc", "心跳加快或不规律");
        symptom3.put("freq", "常见");
        neo4jClient.query(createSymptomCypher).bindAll(symptom3).run();

        Map<String, Object> symptom4 = new HashMap<>();
        symptom4.put("id", 4L);
        symptom4.put("name", "气短");
        symptom4.put("desc", "呼吸困难或气促");
        symptom4.put("freq", "常见");
        neo4jClient.query(createSymptomCypher).bindAll(symptom4).run();

        // 创建风险因素
        String createRiskFactorCypher = "CREATE (rf:RiskFactor {id: $id, name: $name, type: $type}) RETURN rf";
        
        Map<String, Object> risk1 = new HashMap<>();
        risk1.put("id", 1L);
        risk1.put("name", "高盐饮食");
        risk1.put("type", "生活习惯");
        neo4jClient.query(createRiskFactorCypher).bindAll(risk1).run();

        Map<String, Object> risk2 = new HashMap<>();
        risk2.put("id", 2L);
        risk2.put("name", "缺乏运动");
        risk2.put("type", "生活习惯");
        neo4jClient.query(createRiskFactorCypher).bindAll(risk2).run();

        Map<String, Object> risk3 = new HashMap<>();
        risk3.put("id", 3L);
        risk3.put("name", "家族病史");
        risk3.put("type", "遗传");
        neo4jClient.query(createRiskFactorCypher).bindAll(risk3).run();

        Map<String, Object> risk4 = new HashMap<>();
        risk4.put("id", 4L);
        risk4.put("name", "吸烟");
        risk4.put("type", "生活习惯");
        neo4jClient.query(createRiskFactorCypher).bindAll(risk4).run();

        // 创建治疗方案
        String createTreatmentCypher = "CREATE (t:Treatment {id: $id, name: $name, type: $type, description: $desc}) RETURN t";
        
        Map<String, Object> treatment1 = new HashMap<>();
        treatment1.put("id", 1L);
        treatment1.put("name", "药物治疗");
        treatment1.put("type", "药物");
        treatment1.put("desc", "使用降压药物控制血压");
        neo4jClient.query(createTreatmentCypher).bindAll(treatment1).run();

        Map<String, Object> treatment2 = new HashMap<>();
        treatment2.put("id", 2L);
        treatment2.put("name", "生活方式干预");
        treatment2.put("type", "护理");
        treatment2.put("desc", "改善饮食和运动习惯");
        neo4jClient.query(createTreatmentCypher).bindAll(treatment2).run();

        Map<String, Object> treatment3 = new HashMap<>();
        treatment3.put("id", 3L);
        treatment3.put("name", "介入治疗");
        treatment3.put("type", "手术");
        treatment3.put("desc", "冠状动脉介入手术");
        neo4jClient.query(createTreatmentCypher).bindAll(treatment3).run();

        // 创建药物
        String createDrugCypher = "CREATE (d:Drug {id: $id, name: $name, efficacy: $efficacy, usage: $usage, contraindication: $contra}) RETURN d";
        
        Map<String, Object> drug1 = new HashMap<>();
        drug1.put("id", 1L);
        drug1.put("name", "硝苯地平");
        drug1.put("efficacy", "降低血压");
        drug1.put("usage", "每日一次，每次10mg");
        drug1.put("contra", "低血压患者禁用");
        neo4jClient.query(createDrugCypher).bindAll(drug1).run();

        Map<String, Object> drug2 = new HashMap<>();
        drug2.put("id", 2L);
        drug2.put("name", "阿司匹林");
        drug2.put("efficacy", "抗血小板聚集");
        drug2.put("usage", "每日一次，每次100mg");
        drug2.put("contra", "胃溃疡患者慎用");
        neo4jClient.query(createDrugCypher).bindAll(drug2).run();

        Map<String, Object> drug3 = new HashMap<>();
        drug3.put("id", 3L);
        drug3.put("name", "美托洛尔");
        drug3.put("efficacy", "降低心率和血压");
        drug3.put("usage", "每日两次，每次25mg");
        drug3.put("contra", "哮喘患者禁用");
        neo4jClient.query(createDrugCypher).bindAll(drug3).run();

        // 创建关系：疾病-症状
        String createRelationCypher = "MATCH (d:Disease {id: $diseaseId}), (s:Symptom {id: $symptomId}) " +
                "CREATE (d)-[:HAS_SYMPTOM]->(s)";
        
        Map<String, Object> rel1 = new HashMap<>();
        rel1.put("diseaseId", disease1.getId());
        rel1.put("symptomId", 1L);
        neo4jClient.query(createRelationCypher).bindAll(rel1).run();

        Map<String, Object> rel2 = new HashMap<>();
        rel2.put("diseaseId", disease1.getId());
        rel2.put("symptomId", 3L);
        neo4jClient.query(createRelationCypher).bindAll(rel2).run();

        Map<String, Object> rel3 = new HashMap<>();
        rel3.put("diseaseId", disease2.getId());
        rel3.put("symptomId", 2L);
        neo4jClient.query(createRelationCypher).bindAll(rel3).run();

        Map<String, Object> rel4 = new HashMap<>();
        rel4.put("diseaseId", disease2.getId());
        rel4.put("symptomId", 4L);
        neo4jClient.query(createRelationCypher).bindAll(rel4).run();

        // 创建关系：疾病-风险因素
        String createRiskRelationCypher = "MATCH (d:Disease {id: $diseaseId}), (rf:RiskFactor {id: $riskId}) " +
                "CREATE (d)-[:HAS_RISK_FACTOR]->(rf)";
        
        Map<String, Object> riskRel1 = new HashMap<>();
        riskRel1.put("diseaseId", disease1.getId());
        riskRel1.put("riskId", 1L);
        neo4jClient.query(createRiskRelationCypher).bindAll(riskRel1).run();

        Map<String, Object> riskRel2 = new HashMap<>();
        riskRel2.put("diseaseId", disease1.getId());
        riskRel2.put("riskId", 2L);
        neo4jClient.query(createRiskRelationCypher).bindAll(riskRel2).run();

        Map<String, Object> riskRel3 = new HashMap<>();
        riskRel3.put("diseaseId", disease1.getId());
        riskRel3.put("riskId", 3L);
        neo4jClient.query(createRiskRelationCypher).bindAll(riskRel3).run();

        Map<String, Object> riskRel4 = new HashMap<>();
        riskRel4.put("diseaseId", disease2.getId());
        riskRel4.put("riskId", 3L);
        neo4jClient.query(createRiskRelationCypher).bindAll(riskRel4).run();

        Map<String, Object> riskRel5 = new HashMap<>();
        riskRel5.put("diseaseId", disease2.getId());
        riskRel5.put("riskId", 4L);
        neo4jClient.query(createRiskRelationCypher).bindAll(riskRel5).run();

        // 创建关系：疾病-治疗方案
        String createTreatmentRelationCypher = "MATCH (d:Disease {id: $diseaseId}), (t:Treatment {id: $treatmentId}) " +
                "CREATE (d)-[:HAS_TREATMENT]->(t)";
        
        Map<String, Object> treatRel1 = new HashMap<>();
        treatRel1.put("diseaseId", disease1.getId());
        treatRel1.put("treatmentId", 1L);
        neo4jClient.query(createTreatmentRelationCypher).bindAll(treatRel1).run();

        Map<String, Object> treatRel2 = new HashMap<>();
        treatRel2.put("diseaseId", disease1.getId());
        treatRel2.put("treatmentId", 2L);
        neo4jClient.query(createTreatmentRelationCypher).bindAll(treatRel2).run();

        Map<String, Object> treatRel3 = new HashMap<>();
        treatRel3.put("diseaseId", disease2.getId());
        treatRel3.put("treatmentId", 1L);
        neo4jClient.query(createTreatmentRelationCypher).bindAll(treatRel3).run();

        Map<String, Object> treatRel4 = new HashMap<>();
        treatRel4.put("diseaseId", disease2.getId());
        treatRel4.put("treatmentId", 3L);
        neo4jClient.query(createTreatmentRelationCypher).bindAll(treatRel4).run();

        // 创建关系：治疗方案-药物
        String createDrugRelationCypher = "MATCH (t:Treatment {id: $treatmentId}), (d:Drug {id: $drugId}) " +
                "CREATE (t)-[:BELONGS_TO]->(d)";
        
        Map<String, Object> drugRel1 = new HashMap<>();
        drugRel1.put("treatmentId", 1L);
        drugRel1.put("drugId", 1L);
        neo4jClient.query(createDrugRelationCypher).bindAll(drugRel1).run();

        Map<String, Object> drugRel2 = new HashMap<>();
        drugRel2.put("treatmentId", 1L);
        drugRel2.put("drugId", 3L);
        neo4jClient.query(createDrugRelationCypher).bindAll(drugRel2).run();

        Map<String, Object> drugRel3 = new HashMap<>();
        drugRel3.put("treatmentId", 1L);
        drugRel3.put("drugId", 2L);
        neo4jClient.query(createDrugRelationCypher).bindAll(drugRel3).run();

        System.out.println("知识图谱测试数据创建成功！");
        System.out.println("疾病：高血压、冠心病");
        System.out.println("症状：头痛、胸闷、心悸、气短");
        System.out.println("风险因素：高盐饮食、缺乏运动、家族病史、吸烟");
        System.out.println("治疗方案：药物治疗、生活方式干预、介入治疗");
        System.out.println("药物：硝苯地平、阿司匹林、美托洛尔");
    }
}
