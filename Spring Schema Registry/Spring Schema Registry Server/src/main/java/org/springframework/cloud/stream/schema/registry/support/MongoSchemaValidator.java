package org.springframework.cloud.stream.schema.registry.support;

import java.util.List;

import org.springframework.cloud.stream.schema.registry.model.Compatibility;
import org.springframework.cloud.stream.schema.registry.model.MongoSchema;

public interface MongoSchemaValidator {

    boolean isValid(String definition);

    default void validate(String definition) {
        if (!this.isValid(definition)) {
            throw new InvalidSchemaException("Invalid Schema");
        }
    }

    Compatibility compatibilityCheck(String source, String other);

    MongoSchema match(List<MongoSchema> schemas, String definition);

    String getFormat();
}
