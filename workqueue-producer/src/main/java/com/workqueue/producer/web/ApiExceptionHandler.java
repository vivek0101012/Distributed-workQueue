package com.workqueue.producer.web;

import com.workqueue.common.handler.InvalidPayloadException;
import com.workqueue.producer.service.UnknownTaskTypeException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorResponse(String error, String message) {}

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(error, message));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidBody(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .findFirst()
                .orElse("invalid request");
        return respond(HttpStatus.BAD_REQUEST, "invalid_request", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> malformedJson(HttpMessageNotReadableException ex) {
        return respond(HttpStatus.BAD_REQUEST, "malformed_json", "Request body is not valid JSON");
    }

    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<ErrorResponse> invalidPayload(InvalidPayloadException ex) {
        return respond(HttpStatus.BAD_REQUEST, "invalid_payload", ex.getMessage());
    }

    @ExceptionHandler(UnknownTaskTypeException.class)
    public ResponseEntity<ErrorResponse> unknownType(UnknownTaskTypeException ex) {
        return respond(HttpStatus.BAD_REQUEST, "unknown_type", ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> queueUnavailable(DataAccessException ex) {
        return respond(HttpStatus.SERVICE_UNAVAILABLE, "queue_unavailable",
                "The queue is temporarily unavailable. The task was not queued; please retry.");
    }

    // Handlers follow...
}