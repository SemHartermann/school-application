package co.inventorsoft.academy.schoolapplication.util.excepion.excel;

public class EmptyExcelFileException extends IllegalArgumentException {
    public EmptyExcelFileException(String s) {
        super(s);
    }

    public EmptyExcelFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
