package com.audit.mongo.schema.registry.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.audit.mongo.schema.registry.model.Schema;

public interface SchemaRepository extends MongoRepository<Schema, String> {

    List<Schema> findBySubjectOrderByVersion(String subject);
}
