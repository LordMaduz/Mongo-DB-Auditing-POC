package com.audit.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.audit.mongo.annotation.EnableAuditingMongoRepositories;
import com.audit.mongo.annotation.EnableMongoAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableAuditingMongoRepositories(basePackages = "${audit.mongo.base-packages}")
@OpenAPIDefinition(info = @Info(title = "Mongo Auditing Service", version = "1.0", description = "Mongo Auditing Service"))
@EnableMongoAuditing
public class MongoAuditTrailApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoAuditTrailApplication.class, args);
    }

}
