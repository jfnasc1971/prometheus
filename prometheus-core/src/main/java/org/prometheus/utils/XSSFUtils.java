package org.prometheus.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class XSSFUtils {

	public static String getFullReference(XSSFCell cell) {
		String result = null;
		
		if (cell != null) {
			result = String.format("'%s'!%s", cell.getSheet().getSheetName(), cell.getReference());
		}
		
		return result;
	}	
	
	public static final boolean isFormula(XSSFCell cell) {
		boolean result = false;
		
		if (cell != null) {
			return "FORMULA".equalsIgnoreCase(cell.getCellType().name());
		}
		
		return result;
	}
	
	public static final boolean isNumeric(XSSFCell cell) {
		boolean result = false;
		
		if (cell != null) {
			return "NUMERIC".equalsIgnoreCase(cell.getCellType().name());
		}
		
		return result;
	}
	
	public static final boolean isText(XSSFCell cell) {
		boolean result = false;
		
		if (cell != null) {
			return "Text".equalsIgnoreCase(cell.getCellType().name());
		}
		
		return result;
	}
	
}
