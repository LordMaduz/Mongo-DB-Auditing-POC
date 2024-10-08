package com.ruchira.collections.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.audit.mongo.annotation.Auditable;

import lombok.Data;

@Document("person_collection")
@Data
@Auditable(name = "AUDIT_COLLECTION_PERSON")
public class Person{

    @Id
    private String id;
    private String name;
    private School school;
    private Integer increment;

    @Data
    public static class School{
        private String name;
        private List<Class> classList;
    }

    @Data
    public static class Class{
        private String name;
    }

}
