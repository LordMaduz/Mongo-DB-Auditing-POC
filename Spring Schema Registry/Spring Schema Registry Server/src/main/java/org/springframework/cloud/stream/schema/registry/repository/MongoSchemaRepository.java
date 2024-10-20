package org.springframework.cloud.stream.schema.registry.repository;

import java.util.List;

import org.springframework.cloud.stream.schema.registry.model.MongoSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

public interface MongoSchemaRepository extends MongoRepository<MongoSchema, Long> {

    @Transactional
    List<MongoSchema> findBySubjectAndFormatOrderByVersion(String subject, String format);

    @Transactional
    MongoSchema findOneBySubjectAndFormatAndVersion(String subject, String format, Integer version);
}
