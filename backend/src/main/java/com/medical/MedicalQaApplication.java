package com.medical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * 医疗问答系统主应用类
 */
@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "com.medical.repository")
public class MedicalQaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalQaApplication.class, args);
    }

}
