package com.audit.mongo.schema.registry.support;

import java.util.List;
import java.util.Optional;

import org.apache.avro.SchemaParseException;
import org.springframework.stereotype.Component;

import com.audit.mongo.schema.registry.model.Schema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;

import lombok.Getter;

@Component
public class AvroSchemaValidator {

    @Getter
    private final ObjectMapper avroMapper = AvroMapper.builder().build();

    public boolean isValid(String definition) {

        try {
            new org.apache.avro.Schema.Parser().parse(definition);
        } catch (SchemaParseException ex) {
            return false;
        }
        return true;
    }

    public void validate(String definition) {
        try {
            new org.apache.avro.Schema.Parser().parse(definition);
        } catch (SchemaParseException ex) {
            throw new InvalidSchemaException((ex.getMessage()));
        }
    }

    public void validate(Object payload, String definition) {
        org.apache.avro.Schema source = new org.apache.avro.Schema.Parser().parse(definition);
        AvroSchema avroSchema = new AvroSchema(source);
        try {
            avroMapper.writer(avroSchema).writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            throw new InvalidSchemaException(e.getMessage());
        }
    }

    public Optional<Schema> match(List<Schema> schemas, String definition) {
        org.apache.avro.Schema source = new org.apache.avro.Schema.Parser().parse(definition);
        return schemas.stream()
            .filter(schema -> {
                org.apache.avro.Schema target = new org.apache.avro.Schema.Parser().parse(schema.getDefinition());
                return target.equals(source);
            })
            .findFirst();
    }

}
