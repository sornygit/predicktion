package sorny.api.error;

public class InvalidUsernameException extends UserException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
