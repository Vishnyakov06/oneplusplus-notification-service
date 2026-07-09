package com.hh.oneplusplus.exception;

import com.hh.oneplusplus.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NotificationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTemplateNotFound(TemplateNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("TEMPLATE_NOT_FOUND", ex.getMessage()));
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto("FORBIDDEN", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        if (ex instanceof AsyncRequestNotUsableException
                || ex.getClass().getSimpleName().equals("ClientAbortException")) {
            return null;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("INTERNAL_ERROR", "Something went wrong"));
    }
}
