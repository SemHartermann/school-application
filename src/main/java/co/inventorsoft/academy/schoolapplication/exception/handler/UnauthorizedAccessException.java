package co.inventorsoft.academy.schoolapplication.exception.handler;

public class UnauthorizedAccessException extends Throwable {
    public UnauthorizedAccessException(String massage) {
        super(massage);
    }
}
