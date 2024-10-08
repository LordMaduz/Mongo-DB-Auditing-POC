package com.audit.mongo.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ConfigurationProperties(value = "audit.mongo")
@Getter
@Setter
@NoArgsConstructor
public class AuditProperties {
    private List<String> basePackages;
}
