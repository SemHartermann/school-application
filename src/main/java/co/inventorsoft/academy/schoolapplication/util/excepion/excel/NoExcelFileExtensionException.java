package co.inventorsoft.academy.schoolapplication.util.excepion.excel;

public class NoExcelFileExtensionException extends IllegalArgumentException {
    public NoExcelFileExtensionException(String s) {
        super(s);
    }

    public NoExcelFileExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoExcelFileExtensionException(Throwable cause) {
        super(cause);
    }
}
