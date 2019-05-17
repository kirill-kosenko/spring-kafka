package com.demo.serde;

import com.demo.aggregates.CompanyAggregate;
import com.demo.commands.CreateCompanyCommand;
import com.demo.commands.UpdateCompanyCommand;
import com.demo.dto.commands.ValidationResult;
import com.demo.events.CompanyCreatedEvent;
import com.demo.events.CompanyUpdatedEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@SuppressWarnings("DefaultAnnotationParam") // being explicit for the example
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CompanyAggregate.class, name = "CompanyAggregate"),
        @JsonSubTypes.Type(value = CreateCompanyCommand.class, name = "CreateCompanyCommand"),
        @JsonSubTypes.Type(value = UpdateCompanyCommand.class, name = "UpdateCompanyCommand"),
        @JsonSubTypes.Type(value = CompanyCreatedEvent.class, name = "CompanyCreatedEvent"),
        @JsonSubTypes.Type(value = CompanyUpdatedEvent.class, name = "CompanyUpdatedEvent"),
        @JsonSubTypes.Type(value = ValidationResult.class, name = "ValidationResult"),
})
public interface JSONSerdeCompatible {

}
