package org.springframework.cloud.stream.schema.registry.cloud.event;

import org.apache.avro.Schema;

import org.springframework.util.StringUtils;

public class DefaultSubjectNamingStrategy implements SubjectNamingStrategy {

	@Override
	public String toSubject(String subjectNamePrefix, Schema schema) {
		return StringUtils.hasText(subjectNamePrefix) ?
				subjectNamePrefix + "-" + schema.getName().toLowerCase() :
				schema.getName().toLowerCase();
	}

}
