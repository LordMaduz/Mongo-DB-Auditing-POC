package com.audit.mongo.schema.registry.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.audit.mongo.schema.registry.model.Schema;
import com.audit.mongo.schema.registry.repository.SchemaRepository;
import com.audit.mongo.schema.registry.support.AvroSchemaValidator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchemaValidator {

    private final SchemaRepository repository;
    private final AvroSchemaValidator avroSchemaValidator;
    private final AvroSchemaGenerator avroSchemaGenerator;

    public void  validateSchemaWithRegistry(final Object payload) throws JsonMappingException {

        avroSchemaValidator.getAvroMapper()
            .acceptJsonFormatVisitor(payload.getClass(), avroSchemaGenerator);

        org.apache.avro.Schema avroSchema = avroSchemaGenerator.getGeneratedSchema().getAvroSchema();

        Schema schema = new Schema();
        schema.setDefinition(avroSchema.toString());
        schema.setSubject(payload.getClass().getName());

        avroSchemaValidator.validate(schema.getDefinition());
        List<Schema> registeredSchemas = this.repository.findBySubjectOrderByVersion(schema.getSubject());
        final int schemaSize = registeredSchemas.size();
        if (registeredSchemas.isEmpty()) {
            schema.setVersion(1);
            this.repository.save(schema);
        } else {
            Optional<Schema> optionalSchema = avroSchemaValidator.match(registeredSchemas, schema.getDefinition());
            if (optionalSchema.isEmpty()) {
                Schema latestSchema = registeredSchemas.get(schemaSize - 1);
                avroSchemaValidator.validate(payload, latestSchema.getDefinition());
                schema.setVersion(registeredSchemas.get(schemaSize - 1)
                    .getVersion() + 1);
                this.repository.save(schema);
            }
        }
    }
}
