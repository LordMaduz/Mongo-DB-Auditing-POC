package org.springframework.cloud.stream.schema.registry.cloud.event;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.cloud.stream.schema.avro")
public class CloudEventAvroMessageConverterProperties {

	private boolean dynamicSchemaGenerationEnabled;

	private Resource readerSchema;

	/**
	 * The source directory of Apache Avro schema. This schema is used by this converter.
	 * If this schema depends on other schemas consider defining those those dependent
	 * ones in the {@link #schemaImports}
	 * @parameter
	 */
	private Resource[] schemaLocations;

	/**
	 * A list of files or directories that should be loaded first thus making them
	 * importable by subsequent schemas. Note that imported files should not reference
	 * each other.
	 * @parameter
	 */
	private Resource[] schemaImports;

	private String prefix = "vnd";

	private String subjectNamePrefix;

	private boolean ignoreSchemaRegistryServer;

	private Class<? extends SubjectNamingStrategy> subjectNamingStrategy = DefaultSubjectNamingStrategy.class;

	public Resource getReaderSchema() {
		return this.readerSchema;
	}

	public void setReaderSchema(Resource readerSchema) {
		Assert.notNull(readerSchema, "cannot be null");
		this.readerSchema = readerSchema;
	}

	public Resource[] getSchemaLocations() {
		return this.schemaLocations;
	}

	public void setSchemaLocations(Resource[] schemaLocations) {
		Assert.notEmpty(schemaLocations, "cannot be null");
		this.schemaLocations = schemaLocations;
	}

	public boolean isDynamicSchemaGenerationEnabled() {
		return this.dynamicSchemaGenerationEnabled;
	}

	public void setDynamicSchemaGenerationEnabled(boolean dynamicSchemaGenerationEnabled) {
		this.dynamicSchemaGenerationEnabled = dynamicSchemaGenerationEnabled;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Class<?> getSubjectNamingStrategy() {
		return this.subjectNamingStrategy;
	}

	public void setSubjectNamingStrategy(Class<? extends SubjectNamingStrategy> subjectNamingStrategy) {
		Assert.notNull(subjectNamingStrategy, "cannot be null");
		this.subjectNamingStrategy = subjectNamingStrategy;
	}

	public Resource[] getSchemaImports() {
		return this.schemaImports;
	}

	public void setSchemaImports(Resource[] schemaImports) {
		this.schemaImports = schemaImports;
	}

	public String getSubjectNamePrefix() {
		return subjectNamePrefix;
	}

	public void setSubjectNamePrefix(String subjectNamePrefix) {
		this.subjectNamePrefix = subjectNamePrefix;
	}

	public boolean isIgnoreSchemaRegistryServer() {
		return this.ignoreSchemaRegistryServer;
	}

	public void setIgnoreSchemaRegistryServer(boolean ignoreSchemaRegistryServer) {
		this.ignoreSchemaRegistryServer = ignoreSchemaRegistryServer;
	}
}
