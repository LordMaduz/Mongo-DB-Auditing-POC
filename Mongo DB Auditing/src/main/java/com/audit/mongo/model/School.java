package com.audit.mongo.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import com.audit.mongo.annotation.Auditable;

import lombok.Data;

@Document("School_collection")
@Data
@Auditable(name = "AUDIT_COLLECTION_SCHOOL")
public class School extends BaseEntity {

    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String name;

    @Override
    public boolean updateVersion(BaseEntity baseEntity) {
        if (baseEntity instanceof School school) {
            return !school.getName().equals(this.getName());
        }
        return false;
    }
}
