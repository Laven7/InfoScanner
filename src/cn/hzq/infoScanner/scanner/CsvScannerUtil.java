package cn.hzq.infoScanner.scanner;

public class CsvScannerUtil extends ScannerUtil {
    public static final String CSV_POSTFIX = "csv";

    @Override
    public boolean check(String postfix) {
        return CSV_POSTFIX.equals(postfix);
    }


}
