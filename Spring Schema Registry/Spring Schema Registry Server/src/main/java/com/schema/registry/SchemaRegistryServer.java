package com.schema.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.registry.EnableSchemaRegistryServer;

@SpringBootApplication
@EnableSchemaRegistryServer
public class SchemaRegistryServer {

	public static void main(String[] args) {
		SpringApplication.run(SchemaRegistryServer.class, args);
	}

}
