package sorny.api.error;

public class BadPasswordException extends UserException {
    public BadPasswordException(String message) {
        super(message);
    }
}
