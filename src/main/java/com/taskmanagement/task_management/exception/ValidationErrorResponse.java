package com.taskmanagement.task_management.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> validationErrors;
    
    public ValidationErrorResponse(Date timestamp, int status, String error, String message, Map<String, String> validationErrors) {
        super(timestamp, status, error, message, null);
        this.validationErrors = validationErrors;
    }
}
