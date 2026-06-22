package org.prometheus.indicador.sprint;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.prometheus.dto.IndicadorContratoDTO;
import org.prometheus.enums.CellStylesEnum;
import org.prometheus.indicador.Indicador;
import org.prometheus.indicador.ParametrosIndicador;
import org.prometheus.indicador.service.IndicadorSprintIEQRDAService;
import org.prometheus.relatorios.RelatorioContext;
import org.prometheus.utils.XSSFSheetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("IndicadorSprintIEQRDA")
@Scope("prototype")
public class IndicadorSprintIEQRDA extends Indicador {

	@Autowired
	private IndicadorSprintIEQRDAService service;

	public IndicadorSprintIEQRDA(IndicadorContratoDTO indicador, RelatorioContext indicadorContext) {
		super(indicador, indicadorContext);
		pos = 10;
	}

	@Override
	public XSSFCell calcular() {
		escreverTitulo();

		XSSFCell cellVrAjuste = null;
		XSSFCell cellNota = null;
		XSSFCell cellQtQuestoes = null;
		XSSFCell cellQtProfissionais = null;

		ParametrosIndicador param = service.recuperar(context);

		XSSFRow row = null;
		
		if (param != null) {

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Soma Notas Assistido");
			cellNota = XSSFSheetUtils.addCell(row, pos + 2, param.getVrAvaliacaoSprint());
			
			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Questões respondidas");
			cellQtQuestoes = XSSFSheetUtils.addCell(row, pos + 2, param.getQtQuestoesRespondidas());			

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Quantidade Profisionais");
			cellQtProfissionais = XSSFSheetUtils.addCell(row, pos + 2, param.getQtProfissionaisAssistido());

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Fator de Ajuste");
			cellVrAjuste = XSSFSheetUtils.addCell(row, pos + 2, param.getVrFatorAjuste());

			row = context.nextRow();
			XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Peso");
			XSSFSheetUtils.addCell(row, pos + 2, indicador.getVrPesoTipoIndicador());
		}

		row = context.nextRow();
		XSSFSheetUtils.addMergedRegion(context.getSheet(), row, pos, pos + 1, "Valor Indicador");
		XSSFCell result = XSSFSheetUtils.addCell(row, pos + 2, "N/A", CellStylesEnum.NORMAL_ALIGN_CENTER);

		if (cellNota != null) {
			XSSFSheetUtils.setCellFormula(result, String.format("(%s/%s/%s)*%s", //
					cellNota.getReference(), cellQtQuestoes.getReference(), //
					cellQtProfissionais.getReference(), cellVrAjuste.getReference()), //
					CellStylesEnum.DECIMAL);
		}

		return result;
	}

}
