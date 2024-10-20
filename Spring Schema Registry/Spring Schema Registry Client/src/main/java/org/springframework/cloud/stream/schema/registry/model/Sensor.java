package org.springframework.cloud.stream.schema.registry.model;

import lombok.Data;

@Data
public class Sensor {

    private String id;
    private float acceleration;
    private float velocity;
    private float internalTemperature;
    private float externalTemperature;
}
