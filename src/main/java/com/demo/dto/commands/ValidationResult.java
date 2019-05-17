package com.demo.dto.commands;

import com.demo.commands.BaseCommand;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationResult extends BaseCommand<String> {
    public boolean isValid;
    public String message;

    public ValidationResult(String id, boolean isValid) {
        super(id);
        this.isValid = isValid;
    }

    public ValidationResult(String id, String _requestId, boolean isValid) {
        super(id, _requestId);
        this.isValid = isValid;
    }

    public ValidationResult(String id, String requestId, boolean isValid, String message) {
        super(id, requestId);
        this.isValid = isValid;
        this.message = message;
    }
}
