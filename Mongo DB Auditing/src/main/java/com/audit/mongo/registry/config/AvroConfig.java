package com.audit.mongo.schema.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;

@Configuration
public class AvroConfig {

    @Bean
    public AvroSchemaGenerator avroSchemaGenerator() {
        AvroSchemaGenerator schemaGenerator = new AvroSchemaGenerator();
        schemaGenerator.enableLogicalTypes();
        return schemaGenerator;
    }

}

