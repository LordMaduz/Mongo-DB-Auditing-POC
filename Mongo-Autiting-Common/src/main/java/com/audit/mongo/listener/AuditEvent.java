package com.audit.mongo.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

import lombok.Getter;

@Getter
public class AuditEvent<E> extends ApplicationEvent {

    private final MongoMappingEvent<E> mappingEvent;
    public AuditEvent(Object source,MongoMappingEvent<E> mappingEvent) {
        super(source);
        this.mappingEvent = mappingEvent;
    }
}
