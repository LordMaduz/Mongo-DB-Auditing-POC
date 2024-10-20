package com.audit.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruchira.collections.model.School;
import lombok.Data;

@Data
public class PersonUpdatedSchema{
    @JsonProperty(required = true)
    private String name;
    private School school;
    @JsonProperty(required = true)
    private Integer increment;
    @JsonProperty(required = true)
    private Integer decrement;
}
