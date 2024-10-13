package com.audit.mongo.listener;

import static org.springframework.data.mongodb.core.query.SerializationUtils.serializeToJsonSafely;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.reactive.function.client.WebClient;

import com.audit.mongo.model.BaseEntity;
import com.audit.mongo.util.CloudEventUtil;
import com.mongodb.client.model.Filters;
import com.audit.mongo.annotation.Auditable;
import com.audit.mongo.util.InheritableContextHolder;
import com.audit.mongo.util.Util;

import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MongoChangeListener {

    private final ApplicationEventPublisher publisher;
    private final MongoHandler mongoHandler;
    private final WebClient.Builder webclientBuilder;
    private final CloudEventUtil cloudEventUtil;

    private final String DOCUMENT_ID_KEY = "_id";

    @EventListener(condition = "@mongoChangeListener.isEntityAuditable(#event.getSource())")
    public void onAfterConvert(AfterConvertEvent<?> event) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("onAfterConvert: %s, %s", event.getSource(), serializeToJsonSafely(event.getDocument())));
        }
    }

    @EventListener(condition = "@mongoChangeListener.isEntityAuditable(#event.getSource())")
    public void onBeforeSave(BeforeSaveEvent<?> event) throws ClassNotFoundException {
        if (log.isTraceEnabled()) {
            log.trace(String.format("onBeforeSave: %s, %s", event.getSource(), serializeToJsonSafely(event.getDocument())));
        }

        Bson idFilter = Filters.eq(DOCUMENT_ID_KEY, event.getDocument()
            .get(DOCUMENT_ID_KEY));

        Document oldSource = mongoHandler.getDocument(event.getCollectionName(), idFilter);

        InheritableContextHolder.setObject(Util.getIdentityKey(event.getSource()), oldSource);
        log.info(String.format("Before Saving: %s", serializeToJsonSafely(oldSource)));
    }

    @EventListener(condition = "@mongoChangeListener.isEntityAuditable(#event.getSource())")
    public void onAfterSave(AfterSaveEvent<?> event) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("onAfterSave: %s, %s", event.getSource(), serializeToJsonSafely(event.getDocument())));
        }
        log.info(Thread.currentThread()
            .getName());

        Document oldDocument = InheritableContextHolder.getObject(Util.getIdentityKey(event.getSource()), Document.class);
        AuditApplicationEvent auditApplicationEvent = null;
        AuditPayload auditPayload = new AuditPayload();
        auditPayload.setCollectionName(event.getCollectionName());
        auditPayload.setUpdatedDocument(event.getSource());

        Map<String, Object> map = new HashMap<>();

        if (oldDocument != null) {
            final Class<? extends Object> _class = event.getSource()
                .getClass();
            Object object = mongoHandler.getDocument(oldDocument, _class);
            log.info("Base Entity: {}", object);
            if (object instanceof BaseEntity baseEntity) {
                auditApplicationEvent = new AuditApplicationEvent(event.getSource(), baseEntity, event.getCollectionName());
                auditPayload.setOldDocument(baseEntity);
            } else {
                auditApplicationEvent = new AuditApplicationEvent(event.getSource(), null, event.getCollectionName());
                auditPayload.setOldDocument(null);
            }
        } else {
            auditApplicationEvent = new AuditApplicationEvent(event.getSource(), null, event.getCollectionName());
            auditPayload.setOldDocument(null);
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            publisher.publishEvent(auditApplicationEvent);

            final String documentIdValue = Objects.requireNonNull(event.getDocument())
                .get(DOCUMENT_ID_KEY)
                .toString();

            CloudEvent cloudEvent = cloudEventUtil.pojoCloudEvent(auditPayload, documentIdValue);
            var responseEntityMono = webclientBuilder.build()
                .post()
                .uri("http://localhost:8080/mongo-auditing-api/cloud-event")
                .bodyValue(cloudEvent)
                .retrieve()
                .toEntity(String.class);
            responseEntityMono.subscribe(response -> log.info(response.getBody()));
        }
    }

    public Boolean isEntityAuditable(final Object object) {
        Class<?> objectClass = object.getClass();
        return Optional.ofNullable(objectClass.getAnnotation(Auditable.class))
            .isPresent();
    }
}
