package org.springframework.cloud.stream.schema.registry.cloud.event;

import org.apache.avro.Schema;
import org.springframework.cloud.stream.schema.registry.avro.SubjectNamingStrategy;

public class SubjectPrefixOnlyNamingStrategy implements SubjectNamingStrategy {

	@Override
	public String toSubject(String subjectNamePrefix, Schema schema) {
		return subjectNamePrefix;
	}

}
