package org.springframework.cloud.stream.schema.registry.cloud.event;

import org.apache.avro.Schema;

import org.springframework.cloud.stream.schema.registry.avro.SubjectNamingStrategy;
import org.springframework.util.StringUtils;

public class QualifiedSubjectNamingStrategy implements SubjectNamingStrategy {

	@Override
	public String toSubject(String subjectNamePrefix, Schema schema) {
		return StringUtils.hasText(subjectNamePrefix) ?
				subjectNamePrefix + "-" + schema.getFullName().toLowerCase() :
				schema.getFullName().toLowerCase();
	}

}
