package org.springframework.cloud.stream.schema.registry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.stream.schema.registry.config.MongoSchemaServerConfiguration;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MongoSchemaServerConfiguration.class)
public @interface EnableMongoSchemaRegistryServer {

}
