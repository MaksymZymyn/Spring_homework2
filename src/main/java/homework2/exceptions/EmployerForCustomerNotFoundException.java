package homework2.exceptions;

public class EmployerForCustomerNotFoundException extends RuntimeException {
    public EmployerForCustomerNotFoundException(String message) {
        super(message);
    }
}