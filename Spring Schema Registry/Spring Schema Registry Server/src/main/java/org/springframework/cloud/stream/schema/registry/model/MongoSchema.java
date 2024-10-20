package org.springframework.cloud.stream.schema.registry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(value = "SCHEMA_REPOSITORY")
@Data
public class MongoSchema {

    public static final String SEQUENCE_NAME = "schema_sequence";

    @Id
    private Long id;
    private Integer version;
    private String format;
    private String definition;
    private String subject;

}
