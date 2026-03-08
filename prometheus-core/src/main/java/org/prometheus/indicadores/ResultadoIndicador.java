package org.prometheus.indicadores;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class ResultadoIndicador implements Serializable{
	private static final long serialVersionUID = 1L;

	private int rownum;
	private Long nuFaturamento;
	private XSSFCell cellResultado;

	public ResultadoIndicador(int rownum, Long nuFaturamento, XSSFCell cellResultado) {
		super();
		this.rownum = rownum;
		this.nuFaturamento = nuFaturamento;
		this.cellResultado = cellResultado;
	}

	/**
	 * @return the rownum
	 */
	public int getRownum() {
		return rownum;
	}

	/**
	 * @param rownum the rownum to set
	 */
	public void setRownum(int rownum) {
		this.rownum = rownum;
	}

	/**
	 * @return the nuFaturamento
	 */
	public Long getNuFaturamento() {
		return nuFaturamento;
	}

	/**
	 * @param nuFaturamento the nuFaturamento to set
	 */
	public void setNuFaturamento(Long nuFaturamento) {
		this.nuFaturamento = nuFaturamento;
	}

	/**
	 * @return the cellResultado
	 */
	public XSSFCell getCellResultado() {
		return cellResultado;
	}

	/**
	 * @param cellResultado the cellResultado to set
	 */
	public void setCellResultado(XSSFCell cellResultado) {
		this.cellResultado = cellResultado;
	}

}
