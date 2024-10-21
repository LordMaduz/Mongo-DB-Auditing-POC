package org.springframework.cloud.stream.schema.registry.cloud.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import org.springframework.core.io.Resource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.MimeType;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.v1.avro.compact.CloudEvent;

public abstract class AbstractCloudEventAvroMessageConverter extends AbstractMessageConverter {

    public static final String CLOUD_EVENT_DATA_CONTENT_TYPE = "data_contentType";
    private Schema.Parser schemaParser = new Schema.Parser();
    private CloudEventAvroSchemaServiceManager avroSchemaServiceManager;

    protected AbstractCloudEventAvroMessageConverter(MimeType supportedMimeType, CloudEventAvroSchemaServiceManager avroSchemaServiceManager) {
        this(Collections.singletonList(supportedMimeType), avroSchemaServiceManager);
    }

    protected AbstractCloudEventAvroMessageConverter(Collection<MimeType> supportedMimeTypes, CloudEventAvroSchemaServiceManager manager) {
        super(supportedMimeTypes);
        setContentTypeResolver(new OriginalContentTypeResolver());
        this.avroSchemaServiceManager = manager;
    }

    protected CloudEventAvroSchemaServiceManager avroSchemaServiceManager() {
        return this.avroSchemaServiceManager;
    }

    protected Schema parseSchema(Resource r) throws IOException {
        return this.schemaParser.parse(r.getInputStream());
    }

    @Override
    protected boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
        return super.canConvertFrom(message, targetClass) && (message.getPayload() instanceof byte[]);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        Object result;
        try {
            byte[] payload = (byte[]) message.getPayload();

            MimeType mimeType = getContentTypeResolver().resolve(message.getHeaders());
            if (mimeType == null) {
                if (conversionHint instanceof MimeType hintedMimeType) {
                    mimeType = hintedMimeType;
                } else {
                    return null;
                }
            }

            Schema writerSchema = resolveWriterSchemaForDeserialization(mimeType);
            Schema readerSchema = resolveReaderSchemaForDeserialization(targetClass);

            result = avroSchemaServiceManager().readData(targetClass, payload, readerSchema, writerSchema);

            if (result instanceof CloudEvent cloudEvent && message.getHeaders()
                .containsKey(CLOUD_EVENT_DATA_CONTENT_TYPE)) {
                Class<?> cloudEventPayloadClass = Class.forName(cloudEvent.getType());

                String header = message.getHeaders()
                    .get(CLOUD_EVENT_DATA_CONTENT_TYPE, String.class);
                Schema dataObjectwWriterSchema = resolveWriterSchemaForCloudEventDeserialization(header);
                Schema dataObjectReaderSchema = resolveReaderSchemaForDeserialization(cloudEventPayloadClass);

                Object dataObjectResult = avroSchemaServiceManager().readData(cloudEventPayloadClass, cloudEvent.getData()
                    .array(), dataObjectwWriterSchema, dataObjectReaderSchema);
                logger.info(String.format("Cloud Event Payload Transformed Successfully: %s", dataObjectResult));
                if (dataObjectResult == null) {
                    throw new MessageConversionException(message, "Failed to read Cloud Event Payload");
                }
            }

        } catch (IOException e) {
            throw new MessageConversionException(message, "Failed to read payload", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            MimeType hintedContentType = null;
            if (conversionHint instanceof MimeType mimeType) {
                hintedContentType = mimeType;
            }

            if (payload instanceof CloudEvent cloudEvent) {
                Class<?> class_ = Class.forName(cloudEvent.getType());
                Object dataObject = new ObjectMapper().readValue(cloudEvent.getData()
                    .array(), class_);

                ByteArrayOutputStream dataObjectBaos = new ByteArrayOutputStream();
                Schema schema = resolveSchemaForWriting(dataObject, headers, hintedContentType, false);
                DatumWriter<Object> dataObjectWriter = avroSchemaServiceManager().getDatumWriter(class_, schema);
                Encoder encoder = EncoderFactory.get()
                    .binaryEncoder(dataObjectBaos, null);
                dataObjectWriter.write(dataObject, encoder);
                encoder.flush();

                if (schema == null || dataObjectWriter == null) {
                    throw new MessageConversionException("Invalid Payload");
                }
            }

            Schema schema = resolveSchemaForWriting(payload, headers, hintedContentType, true);

            @SuppressWarnings("unchecked") DatumWriter<Object> writer = avroSchemaServiceManager().getDatumWriter(payload.getClass(), schema);
            Encoder encoder = EncoderFactory.get()
                .binaryEncoder(baos, null);
            writer.write(payload, encoder);
            encoder.flush();
        } catch (IOException e) {
            throw new MessageConversionException("Failed to write payload", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    protected abstract Schema resolveSchemaForWriting(Object payload, MessageHeaders headers, MimeType hintedContentType, boolean updateDefaultContentType);

    protected abstract Schema resolveWriterSchemaForDeserialization(MimeType mimeType);

    protected abstract Schema resolveWriterSchemaForCloudEventDeserialization(String contentTypePayload);

    protected abstract Schema resolveReaderSchemaForDeserialization(Class<?> targetClass);

}
