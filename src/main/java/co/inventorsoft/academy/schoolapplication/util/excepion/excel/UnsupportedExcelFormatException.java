package co.inventorsoft.academy.schoolapplication.util.excepion.excel;

public class UnsupportedExcelFormatException extends IllegalArgumentException {
    public UnsupportedExcelFormatException(String s) {
        super(s);
    }
}
