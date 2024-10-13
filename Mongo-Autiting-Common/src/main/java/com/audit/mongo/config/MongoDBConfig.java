package com.audit.mongo.config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.apache.commons.lang3.ObjectUtils;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.audit.mongo.listener.MongoAuditListener;
import com.audit.mongo.listener.MongoChangeListener;
import com.audit.mongo.listener.MongoHandler;
import com.audit.mongo.util.CloudEventUtil;
import com.mongodb.MongoClientSettings;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    private final MongoProperties mongoProperties;

    @Override
    protected void configureClientSettings(final MongoClientSettings.Builder builder) {
        builder
            .codecRegistry(getCodecRegistry());
    }

    private CodecRegistry getCodecRegistry() {
        return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }


    @Bean
    public MongoTransactionManager transactionManager(final MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public MongoAuditListener mongoAuditListener(final MongoHandler mongoHandler) {
        return new MongoAuditListener(mongoHandler);
    }

    @Bean
    public MongoChangeListener mongoChangeListener(final ApplicationEventPublisher applicationEventPublisher, final MongoHandler mongoHandler, final WebClient.Builder builder, final
        CloudEventUtil cloudEventUtil) {
        return new MongoChangeListener(applicationEventPublisher, mongoHandler, builder, cloudEventUtil);
    }

    @Override
    protected String getDatabaseName() {
        final String database = this.mongoProperties.getDatabase();
        if (ObjectUtils.isEmpty(database)) {
            final String uri = this.mongoProperties.getUri();
            return uri.substring(uri.lastIndexOf("/") + 1);
        }
        return database;
    }

}
