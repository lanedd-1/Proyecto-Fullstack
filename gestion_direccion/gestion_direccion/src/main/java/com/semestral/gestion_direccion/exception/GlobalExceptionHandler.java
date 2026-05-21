package com.semestral.gestion_direccion.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    logger.warn("NotFound: {} - {}", req.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), req.getRequestURI(), null));
}

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
    String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
    logger.warn("DataIntegrity: {} - {}", req.getRequestURI(), msg);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), msg, req.getRequestURI(), null));
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + (e.getDefaultMessage() == null ? e.getRejectedValue() : e.getDefaultMessage()))
            .collect(Collectors.toList());
    logger.warn("Validation failed: {} - {}", req.getRequestURI(), details);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation errors", req.getRequestURI(), details));
}

@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ErrorResponseDTO> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
    logger.warn("Invalid JSON: {} - {}", req.getRequestURI(), ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), "Invalid JSON or wrong types", req.getRequestURI(), null));
}

@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
    String msg = String.format("Parameter '%s' with value '%s' cannot be converted to '%s'",
            ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
    logger.warn("TypeMismatch: {} - {}", req.getRequestURI(), msg);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), msg, req.getRequestURI(), null));
}

@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
public ResponseEntity<ErrorResponseDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
    String msg = String.format("HTTP method '%s' not allowed. Allowed: %s", ex.getMethod(), ex.getSupportedHttpMethods());
    logger.warn("MethodNotAllowed: {} - {}", req.getRequestURI(), msg);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.METHOD_NOT_ALLOWED.value(),
                    HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), msg, req.getRequestURI(), null));
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponseDTO> handleAll(Exception ex, HttpServletRequest req) {
    logger.error("Internal error at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error", req.getRequestURI(), null));
}
}
