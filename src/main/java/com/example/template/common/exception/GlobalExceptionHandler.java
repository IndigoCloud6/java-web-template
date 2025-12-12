package com.example.template.common.exception;

import com.example.template.common.response.ApiResponse;
import com.example.template.common.response.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle BizException
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> handleBizException(BizException e) {
        log.error("Business exception: {}", e.getMessage(), e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * Handle validation exceptions (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("Validation error: {}", message);
        return ApiResponse.error(ErrorCode.VALIDATION_ERROR, message);
    }

    /**
     * Handle validation exceptions (BindException)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("Binding error: {}", message);
        return ApiResponse.error(ErrorCode.VALIDATION_ERROR, message);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.error("Constraint violation: {}", message);
        return ApiResponse.error(ErrorCode.VALIDATION_ERROR, message);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication error: {}", e.getMessage());
        if (e instanceof BadCredentialsException) {
            return ApiResponse.error(ErrorCode.INVALID_CREDENTIALS);
        }
        return ApiResponse.error(ErrorCode.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage());
        return ApiResponse.error(ErrorCode.FORBIDDEN);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception e) {
        log.error("System error: {}", e.getMessage(), e);
        return ApiResponse.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

}
