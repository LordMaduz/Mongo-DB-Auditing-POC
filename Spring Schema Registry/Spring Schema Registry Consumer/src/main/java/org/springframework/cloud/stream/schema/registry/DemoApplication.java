package org.springframework.cloud.stream.schema.registry;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.registry.client.EnableSchemaRegistryClient;
import org.springframework.context.annotation.Bean;

import io.cloudevents.v1.avro.compact.CloudEvent;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableSchemaRegistryClient
@Slf4j
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Bean
//	public Consumer<Sensor> process()  {
//		return input -> log.info("[INPUT-RECEIVED]: " + input);
//	}

	@Bean
	public Consumer<CloudEvent> process()  {
		return input -> log.info("[INPUT-RECEIVED]: " + input);
	}

}
