package com.audit.mongo.util;

import static io.cloudevents.core.CloudEventUtils.mapData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.CloudEventExtension;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.BytesCloudEventData;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.extensions.DistributedTracingExtension;
import io.cloudevents.core.provider.ExtensionProvider;
import io.cloudevents.jackson.JsonCloudEventData;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CloudEventUtil {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String PARTITIONING_KEY_EXTENSION = "partitionkey";

    @Value(value = "${server.servlet.context-path}")
    private String servletContextPath;


    public <T> T toObject(CloudEvent cloudEvent, Class<T> type) {
        try {
            if (cloudEvent.getData() instanceof JsonCloudEventData) {
                return OBJECT_MAPPER.treeToValue(((JsonCloudEventData) cloudEvent.getData()).getNode(), type);
            } else if (cloudEvent.getData() instanceof PojoCloudEventData) {
                return Objects.requireNonNull(mapData(cloudEvent, PojoCloudEventDataMapper.from(OBJECT_MAPPER, type)))
                    .getValue();
            } else if (cloudEvent.getData() instanceof BytesCloudEventData bytesCloudEventData) {
                return OBJECT_MAPPER.readValue(bytesCloudEventData.toBytes(), type);
            } else {
                throw new RuntimeException("Unsupported Cloud Event Data Type");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> CloudEventData pojoCloudEventData(T object) {
        return PojoCloudEventData.wrap(object, OBJECT_MAPPER::writeValueAsBytes);
    }

    private <T> CloudEventData bytesCloudEventData(T object) throws JsonProcessingException {
        return BytesCloudEventData.wrap(Objects.requireNonNull(OBJECT_MAPPER.writeValueAsBytes(object)));
    }

    private <T> CloudEventData jsonCloudEventData(T object) {
        return JsonCloudEventData.wrap(OBJECT_MAPPER.valueToTree(object));
    }

    private  <T> CloudEvent cloudEvent(CloudEvent cloudEvent, CloudEventExtension cloudEventExtension){
        return CloudEventBuilder.from(cloudEvent).withExtension(cloudEventExtension).build();
    }

    public <T> CloudEvent pojoCloudEvent(final T object, final String id) {
        return cloudEventBuilder(object, id).withData(pojoCloudEventData(object))
            .build();
    }

    public <T extends Serializable> CloudEvent bytesCloudEvent(final T object, final String id) throws JsonProcessingException {
        return cloudEventBuilder(object, id).withData(bytesCloudEventData(object))
            .build();
    }

    public <T> CloudEvent pojoCloudEvent(final CloudEvent cloudEvent, final T object, final String id) {
        return cloudEventBuilder(cloudEvent, object, id).withData(pojoCloudEventData(object))
            .build();
    }

    public <T> CloudEvent pojoCloudEvent(final HttpHeaders httpHeaders, final T object, final String id) {
        return cloudEventBuilder(httpHeaders, object, id).withData(pojoCloudEventData(object))
            .build();
    }

    public <T> CloudEvent jsonCloudEvent(final T object, final String id) {
        return cloudEventBuilder(object, id).withData(jsonCloudEventData(object))
            .build();
    }

    public <T> CloudEvent jsonCloudEvent(final CloudEvent cloudEvent, final T object, final String id) {
        return cloudEventBuilder(object, id).withData(jsonCloudEventData(object))
            .build();
    }

    public <T> CloudEvent jsonCloudEvent(final HttpHeaders httpHeaders, final T object, final String id) {
        return cloudEventBuilder(httpHeaders, object, id).withData(jsonCloudEventData(object))
            .build();
    }

    private <T extends Serializable> CloudEventBuilder cloudEventBuilder(final T object, final String id) {
        return CloudEventBuilder.v1()
            .withSource(URI.create(servletContextPath))
            .withType(object.getClass().getName())
            .withId(id)
            .withExtension(PARTITIONING_KEY_EXTENSION, id)
            .withTime(ZonedDateTime.now().toOffsetDateTime());
    }

    private <T> CloudEventBuilder cloudEventBuilder(final T object, final String id) {
        return CloudEventBuilder.v1()
            .withSource(URI.create(servletContextPath))
            .withType(object.getClass().getName())
            .withId(id)
            .withExtension(PARTITIONING_KEY_EXTENSION, id)
            .withTime(ZonedDateTime.now().toOffsetDateTime());
    }

    private <T> CloudEventBuilder cloudEventBuilder(final CloudEvent cloudEvent, final T object, String id) {
        return CloudEventBuilder.from(cloudEvent)
            .withSource(URI.create(servletContextPath))
            .withId(id)
            .withExtension(distributedTracingExtension(cloudEvent))
            .withType(object.getClass().getName())
            .withTime(ZonedDateTime.now().toOffsetDateTime());

    }

    private <T> CloudEventBuilder cloudEventBuilder(final HttpHeaders httpHeaders, final T object, String id) {
        return CloudEventHttpUtils.fromHttp(httpHeaders)
            .withSource(URI.create(servletContextPath))
            .withId(id)
            .withType(object.getClass().getTypeName())
            .withTime(ZonedDateTime.now().toOffsetDateTime());
    }


    private DistributedTracingExtension distributedTracingExtension(final CloudEvent event){
        return ExtensionProvider.getInstance()
            .parseExtension(DistributedTracingExtension.class, event);
    }
}
