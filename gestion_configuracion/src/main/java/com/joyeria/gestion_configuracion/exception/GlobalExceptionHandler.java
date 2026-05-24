package com.joyeria.gestion_configuracion.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConfiguracionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ConfiguracionNotFoundException ex, HttpServletRequest req) {
        logger.warn("Configuracion no encontrada | Path: {}", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null));
    }

    @ExceptionHandler(ConfiguracionLongitudInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> handleLongitud(ConfiguracionLongitudInvalidaException ex, HttpServletRequest req) {
        logger.warn("Longitud invalida | Path: {} | Mensaje: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> String.format("Campo '%s': %s", e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());
        logger.warn("Validacion fallida en {} - Errores: {}", req.getRequestURI(), errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, "Errores de validacion", req.getRequestURI(), errores));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleJsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        logger.warn("JSON invalido | Path: {}", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, "JSON invalido o tipos de datos incorrectos", req.getRequestURI(), null));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTipoIncorrecto(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = String.format("El parametro '%s' con valor '%s' no es del tipo correcto", ex.getName(), ex.getValue());
        logger.warn("Tipo incorrecto en {} - {}", req.getRequestURI(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, msg, req.getRequestURI(), null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> handleMetodoNoPermitido(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        logger.warn("Metodo HTTP '{}' no permitido en '{}'", ex.getMethod(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(build(HttpStatus.METHOD_NOT_ALLOWED,
                        "El metodo HTTP '" + ex.getMethod() + "' no esta permitido",
                        req.getRequestURI(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex, HttpServletRequest req) {
        logger.error("Error interno - Tipo: {} | Path: {} | Mensaje: {}",
                ex.getClass().getSimpleName(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", req.getRequestURI(), null));
    }

    private ErrorResponseDTO build(HttpStatus status, String mensaje, String path, List<String> detalles) {
        return new ErrorResponseDTO(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, path, detalles);
    }
}
