package com.schema.registry;

import io.cloudevents.v1.avro.compact.CloudEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudEventConsumer {

    public void process(CloudEvent input){
        log.info("[INPUT-RECEIVED]: " + input);
    }

}
