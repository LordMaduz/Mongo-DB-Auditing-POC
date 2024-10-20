package org.springframework.cloud.stream.schema.registry.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.stream.schema.registry.controllers.ServerController;
import org.springframework.cloud.stream.schema.registry.repository.MongoSchemaRepository;
import org.springframework.cloud.stream.schema.registry.support.AvroMongoSchemaValidator;
import org.springframework.cloud.stream.schema.registry.support.MongoSchemaValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration(
    proxyBeanMethods = false
)
@EnableMongoRepositories(  basePackageClasses = { MongoSchemaRepository.class})
@Import(ServerController.class)
public class MongoSchemaServerConfiguration {

    @Bean
    public Map<String, MongoSchemaValidator> mongoSchemaValidators() {
        Map<String, MongoSchemaValidator> validatorMap = new HashMap();
        validatorMap.put("avro", new AvroMongoSchemaValidator());
        return validatorMap;
    }
}
