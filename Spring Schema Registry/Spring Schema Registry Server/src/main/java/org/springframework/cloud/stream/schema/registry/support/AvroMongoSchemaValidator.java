package org.springframework.cloud.stream.schema.registry.support;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.springframework.cloud.stream.schema.registry.model.Compatibility;
import org.springframework.cloud.stream.schema.registry.model.MongoSchema;

public class AvroMongoSchemaValidator implements MongoSchemaValidator{

    public static final String AVRO_FORMAT = "avro";

    public AvroMongoSchemaValidator() {
    }

    public boolean isValid(String definition) {
        boolean result = true;

        try {
            (new Schema.Parser()).parse(definition);
        } catch (SchemaParseException var4) {
            result = false;
        }

        return result;
    }

    public void validate(String definition) {
        try {
            (new Schema.Parser()).parse(definition);
        } catch (SchemaParseException var3) {
            throw new InvalidSchemaException(var3.getMessage());
        }
    }

    public Compatibility compatibilityCheck(String source, String other) {
        return null;
    }

    public MongoSchema match(List<MongoSchema> schemas, String definition) {
        Schema source = (new Schema.Parser()).parse(definition);

        return schemas.stream().filter(schema-> {
            Schema target = new Schema.Parser().parse(schema.getDefinition());
            return target.equals(source);
        }).findFirst().get();

    }

    public String getFormat() {
        return "avro";
    }
}
