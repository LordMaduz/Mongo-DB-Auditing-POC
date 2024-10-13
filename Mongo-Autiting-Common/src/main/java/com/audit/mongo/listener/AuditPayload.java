package com.audit.mongo.listener;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuditPayload {

    private  Object oldDocument;
    private  String collectionName;
    private Object updatedDocument;
}
