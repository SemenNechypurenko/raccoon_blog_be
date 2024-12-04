package i.exception;

public class MessageAccessDeniedException extends RuntimeException {
    public MessageAccessDeniedException(String message) {
        super(message);
    }
}
