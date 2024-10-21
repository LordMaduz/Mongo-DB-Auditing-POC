package org.springframework.cloud.stream.schema.registry.cloud.event;

import java.io.IOException;
import java.util.Collection;

import org.apache.avro.Schema;

import org.springframework.core.io.Resource;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;


public class CloudEventAvroSchemaMessageConverter extends AbstractCloudEventAvroMessageConverter {

	private Schema schema;

	/**
	 * Create a {@link CloudEventAvroSchemaMessageConverter}. Uses the default {@link MimeType} of
	 * {@code "application/avro"}.
	 * @param manager for schema management
	 */
	public CloudEventAvroSchemaMessageConverter(CloudEventAvroSchemaServiceManager manager) {
		super(new MimeType("application", "avro"), manager);
	}

	/**
	 * Create a {@link CloudEventAvroSchemaMessageConverter}. The converter will be used for the
	 * provided {@link MimeType}.
	 * @param supportedMimeType mime type to be supported by
	 * {@link CloudEventAvroSchemaMessageConverter}
	 * @param manager for schema management
	 */
	public CloudEventAvroSchemaMessageConverter(MimeType supportedMimeType, CloudEventAvroSchemaServiceManager manager) {
		super(supportedMimeType, manager);
	}

	/**
	 * Create a {@link CloudEventAvroSchemaMessageConverter}. The converter will be used for the
	 * provided {@link MimeType}s.
	 * @param supportedMimeTypes the mime types supported by this converter
	 * @param manager for schema management
	 */
	public CloudEventAvroSchemaMessageConverter(Collection<MimeType> supportedMimeTypes, CloudEventAvroSchemaServiceManager manager) {
		super(supportedMimeTypes, manager);
	}

	public Schema getSchema() {
		return this.schema;
	}

	/**
	 * Sets the Apache Avro schema to be used by this converter.
	 * @param schema schema to be used by this converter
	 */
	public void setSchema(Schema schema) {
		Assert.notNull(schema, "schema cannot be null");
		this.schema = schema;
	}

	/**
	 * The location of the Apache Avro schema to be used by this converter.
	 * @param schemaLocation the location of the schema used by this converter.
	 */
	public void setSchemaLocation(Resource schemaLocation) {
		Assert.notNull(schemaLocation, "schema cannot be null");
		try {
			this.schema = parseSchema(schemaLocation);
		}
		catch (IOException e) {
			throw new IllegalStateException("Schema cannot be parsed:", e);
		}
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	protected Schema resolveWriterSchemaForDeserialization(MimeType mimeType) {
		return this.schema;
	}

	@Override
	protected Schema resolveReaderSchemaForDeserialization(Class<?> targetClass) {
		return this.schema;
	}

	@Override
	protected Schema resolveWriterSchemaForCloudEventDeserialization(String contentTypePayload) {return this.schema;}

	@Override
	protected Schema resolveSchemaForWriting(Object payload, MessageHeaders headers,
			MimeType hintedContentType, boolean updateDefaultContentType) {
		return this.schema;
	}

}
