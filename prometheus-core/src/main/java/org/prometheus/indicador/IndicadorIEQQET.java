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

@Service("IndicadorIEQQET")
@Scope("prototype")
public class IndicadorIEQQET extends Indicador {

	@Autowired
	private EntregaService entregaService;

	public IndicadorIEQQET(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
	}

	@Override
	public XSSFCell calcular() {

		escreverTitulo();
		
		List<EntregaDTO> servicosEntreguesEstoque = entregaService.listarServicosEstoque(
				context.getEquipe().getNuEquipe(), context.getFaturamento().getNuFatura());

		XSSFRow row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, "Serviços Entregues");
		XSSFCell qtServicosEntregues = XSSFSheetUtils.addCell(row, 2,
				BigDecimal.valueOf(servicosEntreguesEstoque.size()));

		List<EntregaDTO> servicosContratados = entregaService.listarServicosContratados(
				context.getEquipe().getNuEquipe(), context.getFaturamento().getNuFatura(), 19L);

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, 0, 1, "Serviços Contratados");
		XSSFCell qtServicosContratados = XSSFSheetUtils.addCell(row, 2, BigDecimal.valueOf(servicosContratados.size()));

		// =B30/B31
		row = context.nextRow();
		XSSFCell resultado = XSSFSheetUtils.addCell(row, 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if ("NUMERIC".equals(qtServicosContratados.getCellType().name())
				&& qtServicosContratados.getNumericCellValue() != 0) {
			XSSFSheetUtils.setCellFormula(resultado, //
					String.format("%s/%s", //
							qtServicosEntregues.getReference(), //
							qtServicosContratados.getReference()), //
					CellStylesEnum.DECIMAL5);
		}

		return resultado;
	}

}
