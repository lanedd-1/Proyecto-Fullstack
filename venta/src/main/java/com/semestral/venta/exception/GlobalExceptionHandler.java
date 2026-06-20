package com.semestral.venta.exception;
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
public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    logger.warn("Recurso no encontrado - ID: {} | Path: {} | Mensaje: {}",
            ex.getResourceId(), request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null));
}

@ExceptionHandler(BusinessConflictException.class)
public ResponseEntity<ErrorResponseDTO> handleBusinessConflict(BusinessConflictException ex, HttpServletRequest request) {
    logger.warn("Conflicto de negocio - Path: {} | Mensaje: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null));
}

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
    String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
    logger.warn("Violacion integridad datos - Path: {} | Mensaje: {}", request.getRequestURI(), msg);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, msg, request.getRequestURI(), null));
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> String.format("Campo '%s': %s (valor: '%s')",
                    err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
            .collect(Collectors.toList());
    logger.warn("Validacion fallida - {} {} - Errores: {}", request.getMethod(), request.getRequestURI(), fieldErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, "Errores de validacion en los datos enviados", request.getRequestURI(), fieldErrors));
}

@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ErrorResponseDTO> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
    logger.warn("JSON invalido - Path: {} | Detalle: {}", request.getRequestURI(),
            ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, "Formato JSON invalido o tipos incorrectos", request.getRequestURI(), null));
}

@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponseDTO> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
    logger.warn("Solicitud invalida - Path: {} | Mensaje: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null));
}

@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String msg = String.format("Parametro '%s' con valor '%s' no puede convertirse a '%s'",
            ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");
    logger.warn("Tipo de parametro incorrecto - Path: {} - {}", request.getRequestURI(), msg);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildError(HttpStatus.BAD_REQUEST, msg, request.getRequestURI(), null));
}

@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
public ResponseEntity<ErrorResponseDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    String msg = String.format("Metodo HTTP '%s' no permitido. Metodos validos: %s", ex.getMethod(), ex.getSupportedHttpMethods());
    logger.warn("Metodo no permitido - Path: {} - {}", request.getRequestURI(), msg);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(buildError(HttpStatus.METHOD_NOT_ALLOWED, msg, request.getRequestURI(), null));
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponseDTO> handleAll(Exception ex, HttpServletRequest request) {
    logger.error("Error interno - Tipo: {} | Path: {} | Mensaje: {}", ex.getClass().getSimpleName(), request.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request.getRequestURI(), null));
}

private ErrorResponseDTO buildError(HttpStatus status, String message, String path, List<String> details) {
    return new ErrorResponseDTO(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path, details);
}
}