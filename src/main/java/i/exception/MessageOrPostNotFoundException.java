package i.exception;

public class MessageOrPostNotFoundException extends RuntimeException {
    public MessageOrPostNotFoundException(String message) {
        super(message);
    }
}