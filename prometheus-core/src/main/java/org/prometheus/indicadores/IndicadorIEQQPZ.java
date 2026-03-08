package org.prometheus.indicadores;

import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.EquipeDTO;
import org.prometheus.dto.FaturamentoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.service.EntregaServiceQualidade;
import org.prometheus.utils.SheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndicadorIEQQPZ extends IndicadorBase {

	private static final long serialVersionUID = 1L;

	@Autowired
	protected EntregaServiceQualidade entregaService;

	public IndicadorIEQQPZ() {
		super();
	}

	@Override
	public ResultadoIndicador calcularIndicador(XSSFSheet sheet, int rownum, EquipeDTO equipe, FaturamentoDTO faturamento) {
		
		int pos = 4;
		
		XSSFRow row = SheetUtils.getRow(sheet, rownum++);
		SheetUtils.addMergedRegion(sheet, row, pos, pos+2, "IEQQPZ - Prazo do Serviço", CellStylesEnum.LIGHT_GREY_ALIGN_LEFT_BOLD);

		List<EntregaDTO> servicosEntregues = entregaService
				.listarServicosEntreguesNoPeriodo(equipe.getNuEquipe(), faturamento.getNuFatura());

		row = SheetUtils.getRow(sheet, rownum++);
		SheetUtils.addMergedRegion(sheet, row, pos, pos+1, "Entregues no Período", CellStylesEnum.NORMAL_ALIGN_LEFT);
		XSSFCell cellQtServicosEntregues = SheetUtils.addCell(row, pos+2, BigDecimal.valueOf(servicosEntregues.size()));

		List<EntregaDTO> servicosAtrasados = entregaService
				.listarServicosEntreguesComAtraso(equipe.getNuEquipe(), faturamento.getNuFatura());

		row = SheetUtils.getRow(sheet, rownum++);
		SheetUtils.addMergedRegion(sheet, row, pos, pos+1, "Atrasados", CellStylesEnum.NORMAL_ALIGN_LEFT);
		XSSFCell cellQtServicosAtrasados = SheetUtils.addCell(row, pos+2, BigDecimal.valueOf(servicosAtrasados.size()));

		row = SheetUtils.getRow(sheet, rownum++);
		XSSFCell cellFormulaIEQQPZ = SheetUtils.addCell(row, pos+2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);
		
		if ("NUMERIC".equals(cellQtServicosEntregues.getCellType().name()) && cellQtServicosEntregues.getNumericCellValue() != 0) {
			SheetUtils.setCellFormula(cellFormulaIEQQPZ, //
					String.format("1-(%s/%s)", //
							cellQtServicosAtrasados.getReference(),
							cellQtServicosEntregues.getReference()), //
					CellStylesEnum.DECIMAL);
		}

		return new ResultadoIndicador(rownum, faturamento.getNuFatura(), cellFormulaIEQQPZ);
	}

}
