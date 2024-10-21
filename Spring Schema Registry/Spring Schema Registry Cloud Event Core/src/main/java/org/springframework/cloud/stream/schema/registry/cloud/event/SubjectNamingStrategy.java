package org.springframework.cloud.stream.schema.registry.cloud.event;

import org.apache.avro.Schema;

public interface SubjectNamingStrategy {

	/**
	 * Takes the Avro schema on input and returns the generated subject under which the
	 * schema should be registered.
	 * @param subjectNamePrefix optional subject name prefix
	 * @param schema schema to register
	 * @return subject name
	 */
	String toSubject(String subjectNamePrefix, Schema schema);

}
