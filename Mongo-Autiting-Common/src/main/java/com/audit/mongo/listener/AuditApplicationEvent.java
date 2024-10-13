package com.audit.mongo.listener;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class AuditApplicationEvent extends ApplicationEvent {

    private final Object oldDocument;
    private final String collectionName;
    public AuditApplicationEvent(Object updatedDocument, Object oldDocument, String collectionName) {
        super(updatedDocument);
        this.collectionName = collectionName;
        this.oldDocument = oldDocument;
    }
}
