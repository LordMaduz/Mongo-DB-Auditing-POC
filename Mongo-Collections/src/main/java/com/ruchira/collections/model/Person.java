package com.ruchira.collections.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.audit.mongo.annotation.Auditable;
import com.audit.mongo.model.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document("person_collection")
@Data
@Auditable(name = "AUDIT_COLLECTION_PERSON")
@EqualsAndHashCode(callSuper = false)
public class Person extends BaseEntity {

    @Id
    private String id;
    private String name;
    private School school;
    private Integer increment;

    @Override
    public boolean updateVersion(BaseEntity baseEntity) {
        if(baseEntity instanceof Person person){
            return !person.name.equals(this.name)  ||
                !person.school.getName().equals(this.getSchool().getName());
        }
        return false;
    }

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
