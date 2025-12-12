package com.indigo.template.common.response;

/**
 * Common Error Codes
 */
public enum ErrorCode {

    // Success
    SUCCESS(0, "Success"),

    // Common errors (1000-1999)
    SYSTEM_ERROR(1000, "System error"),
    PARAMETER_ERROR(1001, "Parameter error"),
    VALIDATION_ERROR(1002, "Validation error"),
    RESOURCE_NOT_FOUND(1003, "Resource not found"),

    // Authentication errors (2000-2999)
    UNAUTHORIZED(2001, "Unauthorized"),
    TOKEN_INVALID(2002, "Invalid token"),
    TOKEN_EXPIRED(2003, "Token expired"),
    FORBIDDEN(2004, "Access forbidden"),
    AUTHENTICATION_FAILED(2005, "Authentication failed"),

    // Business errors (3000-3999)
    USER_NOT_FOUND(3001, "User not found"),
    USER_ALREADY_EXISTS(3002, "User already exists"),
    INVALID_CREDENTIALS(3003, "Invalid username or password"),

    // Database errors (4000-4999)
    DATABASE_ERROR(4000, "Database error"),

    // External service errors (5000-5999)
    EXTERNAL_SERVICE_ERROR(5000, "External service error");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
