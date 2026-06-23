package org.prometheus.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.prometheus.indicador.Indicador;

public class XSSFUtils {

	public static boolean isApurado(XSSFCell cell) {
		if (cell != null) {
			return !cell.toString().equalsIgnoreCase(Indicador.NAO_APURADO);
		}
		
		return false;
	}	
	
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
