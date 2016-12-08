package sorny.api.error;

public class IllegalEmailAddressException extends UserException {
    public IllegalEmailAddressException(String email) {
        super("Illegal e-mail: " + email);
    }
}
