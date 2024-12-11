package i.interceptors;

import i.exception.CommentNotFoundException;
import i.exception.EmailNotVerifiedException;
import i.exception.MessageAccessDeniedException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ControllerExceptionHandler {

    /**
     * Handles validation exceptions (MethodArgumentNotValidException) and returns a map of error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        // Iterate through all validation errors and extract field names and error messages
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles EmailNotVerifiedException and returns a message indicating the email is not verified.
     */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
        log.error("Email not verified: {}", ex.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("message", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles MessageAccessDeniedException and returns a message indicating access to the message is denied.
     */
    @ExceptionHandler(MessageAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleMessageAccessDeniedException(MessageAccessDeniedException ex) {
        log.error("Message access denied: {}", ex.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("message", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles CommentNotFoundException and returns a message indicating the comment was not found.
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCommentNotFoundException(CommentNotFoundException ex) {
        log.error("Comment not found: {}", ex.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles malformed or missing request body exceptions (HttpMessageNotReadableException).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Malformed or missing request body: {}", ex.getMessage());
        return new ResponseEntity<>(Collections.singletonMap("message", "Request body is missing or malformed"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Catches all other exceptions and returns a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        return new ResponseEntity<>(Collections.singletonMap("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
