package com.audit.mongo.listener;

import static org.springframework.data.mongodb.core.query.SerializationUtils.serializeToJsonSafely;

import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import com.audit.mongo.annotation.Auditable;
import com.audit.mongo.util.InheritableContextHolder;
import com.audit.mongo.util.Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoAuditListener {

    @TransactionalEventListener()
    @Async(value = "auditListenerTaskExecutor")
    public void onEvent(AuditEvent<?> event) {
        Document document = InheritableContextHolder.getObject(Util.getIdentityKey(event.getSource()), Document.class);

        final String collectionName = event.getMappingEvent()
            .getCollectionName();
        String auditCollectionName = null;
        Optional<Auditable> optionalAuditable = getAuditCollection(event.getSource());

        if(optionalAuditable.isPresent() && ObjectUtils.isNotEmpty(optionalAuditable.get().name())){
            auditCollectionName = optionalAuditable.get().name();
        } else {
            auditCollectionName = "audit_".concat(collectionName);
        }

        log.info(String.format("Audit Collection: %s ", auditCollectionName));
        log.info(String.format("Old Source: %s ", serializeToJsonSafely(document)));
        log.info(String.format("On AuditEvent Received: %s, %s", event.getMappingEvent()
            .getSource(), serializeToJsonSafely(event.getMappingEvent()
            .getDocument())));
        log.info(Thread.currentThread()
            .getName());
    }

    public Optional<Auditable> getAuditCollection(final Object object) {
        Class<?> objectClass = object.getClass();
        return Optional.ofNullable(objectClass.getAnnotation(Auditable.class));
    }
}
