package com.audit.mongo.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

import com.audit.mongo.listener.MongoAuditListener;
import com.audit.mongo.listener.MongoChangeListener;
import com.audit.mongo.listener.MongoHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoDBConfig {

    @Bean
    public MongoTransactionManager transactionManager(final MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public MongoAuditListener mongoAuditListener() {
        return new MongoAuditListener();
    }

    @Bean
    public MongoChangeListener mongoChangeListener(final ApplicationEventPublisher applicationEventPublisher, final MongoHandler mongoHandler) {
        return new MongoChangeListener(applicationEventPublisher, mongoHandler);
    }

}
