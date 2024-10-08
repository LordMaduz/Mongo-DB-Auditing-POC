package com.audit.mongo.listener;

import static org.springframework.data.mongodb.core.query.SerializationUtils.serializeToJsonSafely;

import java.util.Optional;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mongodb.client.model.Filters;
import com.audit.mongo.annotation.Auditable;
import com.audit.mongo.util.InheritableContextHolder;
import com.audit.mongo.util.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MongoChangeListener {

    private final ApplicationEventPublisher publisher;
    private final MongoHandler mongoHandler;

    @EventListener(condition = "@mongoChangeListener.isEntityAuditable(#event.getSource())")
    public void onAfterConvert(AfterConvertEvent<?> event) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("onAfterConvert: %s, %s", event.getSource(), serializeToJsonSafely(event.getDocument())));
        }
    }

    @EventListener(condition = "@mongoChangeListener.isEntityAuditable(#event.getSource())")
    public void onBeforeSave(BeforeSaveEvent<?> event) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("onBeforeSave: %s, %s", event.getSource(), serializeToJsonSafely(event.getDocument())));
        }
        final String ID = "_id";
        Bson idFilter = Filters.eq(ID, event.getDocument().get(ID));
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


        Document document = InheritableContextHolder.getObject(Util.getIdentityKey(event.getSource()), Document.class);

        AuditEvent<?> auditEvent = new AuditEvent<>(event.getSource(), event);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            publisher.publishEvent(auditEvent);
        }
    }

    public Boolean isEntityAuditable(final Object object){
        Class<?> objectClass = object.getClass();
        return Optional.ofNullable(objectClass.getAnnotation(Auditable.class)).isPresent();
    }
}
