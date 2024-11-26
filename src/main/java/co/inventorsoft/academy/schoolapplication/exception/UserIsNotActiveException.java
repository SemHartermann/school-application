package co.inventorsoft.academy.schoolapplication.exception;

public class UserIsNotActiveException extends RuntimeException {
    public UserIsNotActiveException(String message) {
        super(message);
    }
}
