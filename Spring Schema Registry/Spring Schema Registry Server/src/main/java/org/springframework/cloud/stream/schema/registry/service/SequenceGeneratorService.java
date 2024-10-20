package org.springframework.cloud.stream.schema.registry.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Objects;

import org.springframework.cloud.stream.schema.registry.model.DatabaseSequence;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SequenceGeneratorService {

    private final MongoTemplate mongoTemplate;

    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoTemplate.findAndModify(query(where("_id").is(seqName)),
            new Update().inc("seq",1), options().returnNew(true).upsert(true),
            DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;

    }
}
