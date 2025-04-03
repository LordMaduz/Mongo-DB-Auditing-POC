package com.schema.registry.config;

import java.util.function.Consumer;

import com.schema.registry.CloudEventConsumer;
import com.schema.registry.model.Sensor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.cloudevents.v1.avro.compact.CloudEvent;

@Configuration
public class Config {

    @Bean
    public CloudEventConsumer cloudEventConsumer() {
        return new CloudEventConsumer();
    }

    @Bean
    public Consumer<CloudEvent> process(final CloudEventConsumer cloudEventConsumer) {
        return input -> {
            try {
                if (isSensorEvent(input)) {
                    cloudEventConsumer.process(input);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Boolean isSensorEvent(CloudEvent cloudEvent) throws ClassNotFoundException {
        return Class.forName(cloudEvent.getType())
            .equals(Sensor.class);
    }

}
