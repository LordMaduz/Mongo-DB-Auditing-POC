package com.audit.mongo.handler;

import java.lang.annotation.Annotation;

import org.springframework.data.mongodb.repository.config.MongoRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import com.audit.mongo.annotation.EnableAuditingMongoRepositories;

public class AuditingMongoRepositoriesRegistrar extends AuditingRepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableAuditingMongoRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new MongoRepositoryConfigurationExtension();
    }
}
