package cn.hzq.infoScanner.scanner;

public class ExcelScannerUtil extends ScannerUtil {
	public static final String OFFICE_EXCEL_2003_POSTFIX = "xls";
	public static final String OFFICE_EXCEL_2010_POSTFIX = "xlsx";

	@Override
	public boolean check(String postfix) {
		return OFFICE_EXCEL_2003_POSTFIX.equals(postfix)
				|| OFFICE_EXCEL_2010_POSTFIX.equals(postfix);
	}



}