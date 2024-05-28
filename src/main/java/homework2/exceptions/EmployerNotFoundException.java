package homework2.exceptions;

public class EmployerNotFoundException extends RuntimeException {
    public EmployerNotFoundException(String message) {
        super(message);
    }
}