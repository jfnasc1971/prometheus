package org.prometheus.relatorios;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.CicloTrabalhoDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;

public class RelatorioContext {

	private EquipeDTO equipe;
	private CicloTrabalhoDTO ciclo;
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private Integer rownum;
	private FaturamentoDTO faturamento;
	private Map<Long, Map<String, XSSFCell>> indicadoresFaturamento;

	public RelatorioContext(EquipeDTO equipe, XSSFWorkbook wb, XSSFSheet sheet) {
		this(equipe, wb, sheet, 0);
	}

	public RelatorioContext(EquipeDTO equipe, XSSFWorkbook wb, XSSFSheet sheet, Integer rownum) {
		this.wb = wb;
		this.sheet = sheet;
		this.rownum = rownum;
		this.equipe = equipe;
	}

	public final XSSFRow getRow(Integer rownum) {
		XSSFRow row = sheet.getRow(rownum);
		
		if (row == null) {
			row = sheet.createRow(rownum);
			this.rownum = rownum;
		}
		
		return row;
	}
	
	public final void addEmptyRow() {
		nextRow();
	}
	
	public final XSSFRow nextRow() {
		
		XSSFRow row = sheet.getRow(this.rownum);
		
		if (row == null) {
			row = sheet.createRow(this.rownum);
		}
		
		this.rownum += 1;
		
		return row;
	}

	public void addIndicador(Long nuFaturamento, String siglaIndicador, XSSFCell valorIndicador) {
		if (indicadoresFaturamento == null) {
			indicadoresFaturamento = new LinkedHashMap<Long, Map<String, XSSFCell>>();
		}
		if (indicadoresFaturamento.get(nuFaturamento) == null) {
			indicadoresFaturamento.put(nuFaturamento, new LinkedHashMap<String, XSSFCell>());
		}
		indicadoresFaturamento.get(nuFaturamento).put(siglaIndicador, valorIndicador);
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

	/**
	 * @return the wb
	 */
	public XSSFWorkbook getWb() {
		return wb;
	}

	/**
	 * @param wb the wb to set
	 */
	public void setWb(XSSFWorkbook wb) {
		this.wb = wb;
	}

	/**
	 * @return the equipe
	 */
	public EquipeDTO getEquipe() {
		return equipe;
	}

	/**
	 * @param equipe the equipe to set
	 */
	public void setEquipe(EquipeDTO equipe) {
		this.equipe = equipe;
	}

	/**
	 * @return the faturamento
	 */
	public FaturamentoDTO getFaturamento() {
		return faturamento;
	}

	/**
	 * @param faturamento the faturamento to set
	 */
	public void setFaturamento(FaturamentoDTO faturamento) {
		this.faturamento = faturamento;
	}

	/**
	 * @return the indicadoresFaturamento
	 */
	public Map<Long, Map<String, XSSFCell>> getIndicadoresFaturamento() {
		return indicadoresFaturamento;
	}

	/**
	 * @param indicadoresFaturamento the indicadoresFaturamento to set
	 */
	public void setIndicadoresFaturamento(Map<Long, Map<String, XSSFCell>> indicadoresFaturamento) {
		this.indicadoresFaturamento = indicadoresFaturamento;
	}

	/**
	 * @return the ciclo
	 */
	public CicloTrabalhoDTO getCiclo() {
		return ciclo;
	}

	/**
	 * @param ciclo the ciclo to set
	 */
	public void setCiclo(CicloTrabalhoDTO ciclo) {
		this.ciclo = ciclo;
	}
	
}
