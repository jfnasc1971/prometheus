package org.prometheus.relatorios.demanda;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prometheus.dto.DemandaDTO;
import org.prometheus.dto.SolicitacaoDemandaDTO;
import org.prometheus.relatorios.Relatorio;
import org.prometheus.service.DemandaService;
import org.prometheus.service.SolicitacaoDemandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbaDemanda extends Relatorio {

	private static final int LAYOUT_COLUMNS_COUNT = 14;

	@Autowired
	private DemandaService demandaService;

	@Autowired
	private SolicitacaoDemandaService solicitacaoDemandaService;

	public void write(XSSFWorkbook workBook, String coDemanda) throws Exception {

		DemandaDTO demanda = demandaService.listar(coDemanda);

		carregarFontes(workBook);

		carregarEstilos(workBook);

		XSSFSheet sheet = workBook.createSheet(coDemanda);

		sheet.setDisplayGridlines(false);

		setAllColumnsWidth(sheet, LAYOUT_COLUMNS_COUNT, 70);

		XSSFRow row = null;

		row = addNewRow(sheet);
		addMergedRegion(sheet, row, 0, LAYOUT_COLUMNS_COUNT, demanda.getCoDemanda(), ALIGN_CENTER_GREY_40_PERCENT_BOLD);

		addBlank(sheet);

		row = addNewRow(sheet);
		addCell(row, 0, "Sistema", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addCell(row, 1, demanda.getSgSistema());

		addBlank(sheet);

		List<SolicitacaoDemandaDTO> solicitacoes = solicitacaoDemandaService.listar(coDemanda);

		row = addNewRow(sheet);
		addCell(row, 0, "Código", ALIGN_CENTER_GREY_25_PERCENT_BOLD);
		addMergedRegion(sheet, row, 1, 2, "Resumo", ALIGN_CENTER_GREY_25_PERCENT_BOLD);

		for (SolicitacaoDemandaDTO solicitacao : solicitacoes) {
			row = addNewRow(sheet);
			addCell(row, 0, solicitacao.getCoSolicitacao());
			addMergedRegion(sheet, row, 1, 2, solicitacao.getNoSolicitacao());
		}

	}

}
