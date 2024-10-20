package com.audit.mongo.schema.registry.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(value = "schema_collection")
@Data
public class Schema {

    @Id
    private String id;

    private Integer version;

    private String subject;

    private String definition;

}
