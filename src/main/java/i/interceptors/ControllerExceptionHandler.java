package i.interceptors;

import i.exception.EmailNotVerifiedException;
import i.exception.MessageAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions
            (MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
        return new ResponseEntity<>(Collections.singletonMap("message", ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MessageAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleMessageAccessDeniedException(MessageAccessDeniedException ex) {
        return new ResponseEntity<>(Collections.singletonMap("message", ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>>
    handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(Collections.singletonMap("message", "Request body is missing or malformed"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}