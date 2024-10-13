package com.audit.mongo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.audit.mongo.listener.AuditPayload;
import com.audit.mongo.util.CloudEventUtil;

import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/cloud-event")
@Slf4j
@RequiredArgsConstructor
public class CloudEventController {

    private final CloudEventUtil cloudEventUtil;
    private final WebClient.Builder builder;

    @PostMapping
    public ResponseEntity<String> test(@RequestBody CloudEvent cloudEvent) {
        AuditPayload auditEvent = cloudEventUtil.toObject(cloudEvent, AuditPayload.class);
        log.info(auditEvent.toString());
        return builder.build()
            .post()
            .uri("http://localhost:8080/mongo-auditing-api/cloud-event/test")
            .bodyValue(cloudEvent)
            .retrieve()
            .toEntity(String.class).block();
    }

    @PostMapping("/test")
    public ResponseEntity<String> test2(@RequestBody CloudEvent cloudEvent) {
        AuditPayload auditEvent = cloudEventUtil.toObject(cloudEvent, AuditPayload.class);
        log.info(auditEvent.toString());
        return ResponseEntity.ok("SUCCESS");
    }

}
