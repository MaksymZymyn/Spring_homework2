package homework2.exceptions;

public class SameAccountException extends RuntimeException {
    public SameAccountException(String message) {
        super(message);
    }
}
