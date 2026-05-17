package org.prometheus.indicador;

import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.EntregaDTO;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.service.EntregaService;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorIEQQL")
@Scope("prototype")
public class IndicadorIEQQL extends Indicador {

	@Autowired
	private EntregaService entregaService;

	public IndicadorIEQQL(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
	}

	@Override
	public XSSFCell calcular() {

		escreverTitulo();
		
		List<EntregaDTO> servicosEntregues = entregaService.listarServicosEntregues(
				context.getEquipe().getNuEquipe(), context.getFaturamento().getNuFatura());

		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, "Serviços Entregues no Período");
		XSSFCell qtServicosEntregues = XSSFSheetUtils.addCell(row, 2,
				BigDecimal.valueOf(servicosEntregues.size()));

		List<EntregaDTO> servicosDevolvidos = entregaService.listarServicosDevolvidos(
				context.getEquipe().getNuEquipe(), context.getFaturamento().getNuFatura());

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, "Serviços Devolvidos no Período");
		XSSFCell qtServicosDevolvidos = XSSFSheetUtils.addCell(row, 2, BigDecimal.valueOf(servicosDevolvidos.size()));

		// ==B10/(B10+B11)
		row = context.nextRow();
		XSSFCell resultado = XSSFSheetUtils.addCell(row, 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if ("NUMERIC".equals(qtServicosEntregues.getCellType().name())
				&& qtServicosEntregues.getNumericCellValue() != 0) {
			XSSFSheetUtils.setCellFormula(resultado, //
					String.format("%s/(%s+%s)", //
							qtServicosEntregues.getReference(), //
							qtServicosEntregues.getReference(), //
							qtServicosDevolvidos.getReference()), //
					CellStylesEnum.DECIMAL5);
		}

		return resultado;
	}

}
