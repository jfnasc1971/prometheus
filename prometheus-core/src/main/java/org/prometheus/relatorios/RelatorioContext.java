package org.prometheus.relatorios;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class RelatorioContext {

	private XSSFSheet sheet;
	private Integer rownum;

	public RelatorioContext(XSSFSheet sheet) {
		this(sheet, 0);
	}

	public RelatorioContext(XSSFSheet sheet, Integer rownum) {
		this.sheet = sheet;
		this.rownum = rownum;
	}

	public final XSSFRow nextRow() {
		XSSFRow row = sheet.getRow(rownum++);
		if (row == null) {
			row = sheet.createRow(rownum++);
		}
		return row;
	}

	/**
	 * @return the sheet
	 */
	public XSSFSheet getSheet() {
		return sheet;
	}

	/**
	 * @param sheet the sheet to set
	 */
	public void setSheet(XSSFSheet sheet) {
		this.sheet = sheet;
	}

	/**
	 * @return the rownum
	 */
	public Integer getRownum() {
		return rownum;
	}

	/**
	 * @param rownum the rownum to set
	 */
	public void setRownum(Integer rownum) {
		this.rownum = rownum;
	}

}
