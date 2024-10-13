package com.audit.mongo.listener;

import static org.springframework.data.mongodb.core.query.SerializationUtils.serializeToJsonSafely;

import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.bson.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import com.audit.mongo.annotation.Auditable;
import com.audit.mongo.model.BaseEntity;
import com.audit.mongo.util.InheritableContextHolder;
import com.audit.mongo.util.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MongoAuditListener {

    private final MongoHandler mongoHandler;

    @TransactionalEventListener()
    @Async(value = "auditListenerTaskExecutor")
    public void onEvent(AuditApplicationEvent event) {
        Document oldDocument = InheritableContextHolder.getObject(Util.getIdentityKey(event.getSource()), Document.class);

        if (oldDocument != null) {
            final Class<?> _class = event.getSource().getClass();
            Object oldObject = mongoHandler.getDocument(oldDocument, _class);
            Object updatedObject = event.getSource();
            log.info("Base Entity: {}", oldObject);
            if (oldObject instanceof BaseEntity oldBaseEntity &&
                updatedObject instanceof BaseEntity updatedBaseEntity) {
                final Boolean isVersionUpdated = updatedBaseEntity.updateVersion(oldBaseEntity);
                log.info("IS Version Updated: {}", isVersionUpdated);
            }
        }

        final String collectionName = event.getCollectionName();
        String auditCollectionName = null;
        Optional<Auditable> optionalAuditable = getAuditCollection(event.getSource());

        if (optionalAuditable.isPresent() && ObjectUtils.isNotEmpty(optionalAuditable.get()
            .name())) {
            auditCollectionName = optionalAuditable.get()
                .name();
        } else {
            auditCollectionName = "audit_" .concat(collectionName);
        }

        log.info(String.format("Audit Collection: %s ", auditCollectionName));
        log.info(String.format("Old Source: %s ", serializeToJsonSafely(oldDocument)));
        log.info(String.format("On AuditEvent Received: %s, %s", event.getSource()
            , serializeToJsonSafely(event.getOldDocument())));
        log.info(Thread.currentThread()
            .getName());
    }

    public Optional<Auditable> getAuditCollection(final Object object) {
        Class<?> objectClass = object.getClass();
        return Optional.ofNullable(objectClass.getAnnotation(Auditable.class));
    }
}
