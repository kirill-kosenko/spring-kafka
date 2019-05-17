package com.demo.aggregates;

import com.demo.commands.BaseCommand;
import com.demo.commands.CreateCompanyCommand;
import com.demo.commands.UpdateCompanyCommand;
import com.demo.dto.commands.ValidationResult;
import com.demo.events.BaseEvent;
import com.demo.events.CompanyCreatedEvent;
import com.demo.events.CompanyUpdatedEvent;
import com.demo.serde.JSONSerdeCompatible;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAggregate implements JSONSerdeCompatible {

    public String name;
    public String address;

    public void apply(BaseEvent<String> event) {
        if (event instanceof CompanyCreatedEvent) {
            apply((CompanyCreatedEvent) event);
        } else if (event instanceof CompanyUpdatedEvent) {
            apply((CompanyUpdatedEvent) event);
        }
    }

    public void apply(CompanyCreatedEvent companyCreatedEvent) {
        if (null != companyCreatedEvent.name) {
            this.name = companyCreatedEvent.name;
        } if (null != companyCreatedEvent.address) {
            this.address = companyCreatedEvent.address;
        }
    }

    public void apply(CompanyUpdatedEvent companyUpdatedEvent) {
        if (null != companyUpdatedEvent.name) {
            this.name = companyUpdatedEvent.name;
        } if (null != companyUpdatedEvent.address) {
            this.address = companyUpdatedEvent.address;
        }
    }

    public ValidationResult validate(BaseCommand<String> command) {
        if (command instanceof CreateCompanyCommand) {
            return validate((CreateCompanyCommand) command);
        } else if (command instanceof UpdateCompanyCommand) {
            return validate((UpdateCompanyCommand) command);
        }
        return new ValidationResult(command.id, command._requestId, false, "Unknown error");
    }

    private ValidationResult validate(CreateCompanyCommand command) {
        return new ValidationResult(command.id, command._requestId, true);
    }

    private ValidationResult validate(UpdateCompanyCommand command) {
        return new ValidationResult(command.id, command._requestId, true);
    }
}
