package com.schema.registry;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import com.schema.registry.model.Sensor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.v1.avro.compact.CloudEvent;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private final Random random = new Random();
	private final StreamBridge streamBridge;

	@GetMapping
	public String sendRandomMessage() throws JsonProcessingException {
		//streamBridge.send("supplier-out-0", randomSensor());
		//CloudEventAvroMessageConverterAutoConfiguration
		//AvroMessageConverterAutoConfiguration
		//AvroSchemaRegistryClientMessageConverter
		CloudEvent cloudEvent = cloudEvent(randomSensor());
		streamBridge.send("supplier-out-0", cloudEvent);

		return "ok, have fun with v2 payload!";
	}

	private Sensor randomSensor() {
		Sensor sensor = new Sensor();
		sensor.setId(UUID.randomUUID() + "-v2");
		sensor.setAcceleration(random.nextFloat() * 10);
		sensor.setVelocity(random.nextFloat() * 100);
		sensor.setInternalTemperature(random.nextFloat() * 50);
		sensor.setExternalTemperature(random.nextFloat() * 50);
		return sensor;
	}

	private CloudEvent cloudEvent(final Object object) throws JsonProcessingException {
		return CloudEvent.newBuilder()
			.setId(UUID.randomUUID().toString())
			.setType(object.getClass().getName())
			.setExtensions(Map.of("KEY", "VALUE"))
			.setData(ByteBuffer.wrap(new ObjectMapper().writeValueAsBytes(object)))
			.setSource("Spring Schema Registry Client")
			.setSubject("Sensor Data")
			.setTime(Instant.now())
			.build();
	}

}
