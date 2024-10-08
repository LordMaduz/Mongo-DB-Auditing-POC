package com.audit.mongo.listener;

import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.model.Filters;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MongoHandler {

    private final MongoTemplate mongoTemplate;

    public Document getDocument(final String collectionName, final Bson... bson){
        return mongoTemplate.getCollection(collectionName).find(Filters.and(bson)).first();
    }
}
