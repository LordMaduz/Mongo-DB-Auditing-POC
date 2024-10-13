package com.audit.mongo.model;

public abstract class BaseEntity {
    public boolean updateVersion(BaseEntity baseEntity) {
        return false;
    }
}
