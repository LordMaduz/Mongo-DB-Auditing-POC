/*
 * Copyright 2016-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.schema.registry.cloud.event;

import java.lang.reflect.Constructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.stream.schema.registry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import io.cloudevents.v1.avro.compact.CloudEvent;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(value = CloudEvent.class)
@EnableConfigurationProperties({ CloudEventAvroMessageConverterProperties.class })
@Import(CloudEventAvroSchemaServiceManagerImpl.class)
public class CloudEventAvroMessageConverterAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(CloudEventAvroSchemaRegistryClientMessageConverter.class)
	public CloudEventAvroSchemaRegistryClientMessageConverter cloudEventAvroSchemaRegistryClientMessageConverter(
			SchemaRegistryClient schemaRegistryClient,
			CloudEventAvroSchemaServiceManager avroSchemaServiceManager,
			CloudEventAvroMessageConverterProperties cloudEventAvroMessageConverterProperties) {

		CloudEventAvroSchemaRegistryClientMessageConverter cloudEventAvroSchemaRegistryClientMessageConverter =
				new CloudEventAvroSchemaRegistryClientMessageConverter(schemaRegistryClient, cacheManager(), avroSchemaServiceManager);

		cloudEventAvroSchemaRegistryClientMessageConverter.setDynamicSchemaGenerationEnabled(
				cloudEventAvroMessageConverterProperties.isDynamicSchemaGenerationEnabled());

		if (cloudEventAvroMessageConverterProperties.getReaderSchema() != null) {
			cloudEventAvroSchemaRegistryClientMessageConverter.setReaderSchema(cloudEventAvroMessageConverterProperties.getReaderSchema());
		}
		if (!ObjectUtils.isEmpty(cloudEventAvroMessageConverterProperties.getSchemaLocations())) {
			cloudEventAvroSchemaRegistryClientMessageConverter.setSchemaLocations(cloudEventAvroMessageConverterProperties.getSchemaLocations());
		}
		if (!ObjectUtils.isEmpty(cloudEventAvroMessageConverterProperties.getSchemaImports())) {
			cloudEventAvroSchemaRegistryClientMessageConverter.setSchemaImports(cloudEventAvroMessageConverterProperties.getSchemaImports());
		}
		cloudEventAvroSchemaRegistryClientMessageConverter.setPrefix(cloudEventAvroMessageConverterProperties.getPrefix());

		if (cloudEventAvroMessageConverterProperties.isIgnoreSchemaRegistryServer()) {
			cloudEventAvroSchemaRegistryClientMessageConverter.setIgnoreSchemaRegistryServer(true);
		}

		try {
			Class<?> clazz = cloudEventAvroMessageConverterProperties.getSubjectNamingStrategy();
			Constructor constructor = ReflectionUtils.accessibleConstructor(clazz);
			cloudEventAvroSchemaRegistryClientMessageConverter.setSubjectNamingStrategy(
					(SubjectNamingStrategy) constructor.newInstance());
		}
		catch (Exception ex) {
			throw new IllegalStateException("Unable to create SubjectNamingStrategy "
					+ cloudEventAvroMessageConverterProperties.getSubjectNamingStrategy().toString(), ex);
		}
		cloudEventAvroSchemaRegistryClientMessageConverter.setSubjectNamePrefix(cloudEventAvroMessageConverterProperties.getSubjectNamePrefix());

		return cloudEventAvroSchemaRegistryClientMessageConverter;
	}

	@Bean
	@ConditionalOnMissingBean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}

}
