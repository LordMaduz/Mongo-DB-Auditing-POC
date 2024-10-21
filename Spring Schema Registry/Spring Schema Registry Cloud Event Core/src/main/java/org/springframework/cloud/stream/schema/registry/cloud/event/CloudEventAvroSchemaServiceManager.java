package org.springframework.cloud.stream.schema.registry.cloud.event;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

public interface CloudEventAvroSchemaServiceManager {

	/**
	 * get {@link Schema}.
	 * @param clazz {@link Class} for which schema generation is required
	 * @return returns avro schema for given class
	 */
	Schema getSchema(Class<?> clazz);

	/**
	 * get {@link DatumWriter}.
	 * @param type {@link Class} of java object which needs to be serialized
	 * @param schema {@link Schema} of object which needs to be serialized
	 * @return datum writer which can be used to write Avro payload
	 */
	DatumWriter<Object> getDatumWriter(Class<? extends Object> type, Schema schema);

	/**
	 * get {@link DatumReader}.
	 * @param type {@link Class} of java object which needs to be serialized
	 * @param schema {@link Schema} default schema of object which needs to be de-serialized
	 * @param writerSchema {@link Schema} writerSchema provided at run time
	 * @return datum reader which can be used to read Avro payload
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	DatumReader<Object> getDatumReader(Class<? extends Object> type, Schema schema, Schema writerSchema);

	/**
	 * read data from avro type payload {@link DatumReader}.
	 * @param targetClass {@link Class} of java object which needs to be serialized
	 * @param payload {@link byte} serialized payload of object which needs to be de-serialized
	 * @param readerSchema {@link Schema} readerSchema of object which needs to be de-serialized
	 * @param writerSchema {@link Schema} writerSchema used to while serializing payload
	 * @return java object after reading Avro Payload
	 * @throws IOException in case of error
	 */
	Object readData(Class<? extends Object> targetClass, byte[] payload, Schema readerSchema, Schema writerSchema)
																	throws IOException;
}
