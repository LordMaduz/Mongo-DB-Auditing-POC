package org.springframework.cloud.stream.schema.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongoSchemaRegistryServer
public class SpringSchemaRegistryServer {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
