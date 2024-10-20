package com.audit.mongo.schema.registry.support;

public class InvalidSchemaException extends RuntimeException{
    public InvalidSchemaException(String message) {
        super(message);
    }
}
