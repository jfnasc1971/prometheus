package org.prometheus.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class InsumosFaturamentoDTO {

    private Long nuEquipe;
    private Long nuFatura; // Faturamento
    private XSSFCell qtDiasFaturamento; // Qtd. Dias Fat.
    private BigDecimal vrPreco; // Preço
    private Map<String, BigDecimal> precosPerfil; // Lista de preços por perfil
    private Map<String, BigDecimal> qtEfetivaPerfil; // Quantidade efetiva por perfil
    private Map<Long, BigDecimal> qtDiasExtraPerfil; // Quantidade extras por perfil
    private Map<Long, BigDecimal> qtAusenciasPerfil; // Quantidade ausencias por perfil
    private XSSFCell qtEfetiva; // Qtd. Efetiva
    private XSSFCell qtEfetivaTotal; // Qtd. Efetiva
    private XSSFCell qtDiasExtra; // Qtd. Dias Extras
    private XSSFCell vrExtras; // Valor Extras
    private XSSFCell vrAusencias; // Valor Ausências
    private XSSFCell vrPago; // Valor Pago
    private XSSFCell qtDiasAusencia; // Qtd. Dias Ausências
    private XSSFCell vrPrevistoTermo; // Vr. Previsto Termo
    private XSSFCell vrEquipe; // Vr. Previsto Termo
    private XSSFCell vrFns; // FNS
    private boolean considerarFNS;
    private Long nuPerfil;
    private List<VigenciaMembrosDTO> vigencias;
    private XSSFCell pcDescontoProporcional;
    private XSSFCell vrDescontoProporcional;

    /**
     * @return the nuFatura
     */
    public Long getNuFatura() {
        return nuFatura;
    }

    /**
     * @param nuFatura the nuFatura to set
     */
    public void setNuFatura(Long nuFatura) {
        this.nuFatura = nuFatura;
    }

    /**
     * @return the qtDiasFaturamento
     */
    public XSSFCell getQtDiasFaturamento() {
        return qtDiasFaturamento;
    }

    /**
     * @param qtDiasFaturamento the qtDiasFaturamento to set
     */
    public void setQtDiasFaturamento(XSSFCell qtDiasFaturamento) {
        this.qtDiasFaturamento = qtDiasFaturamento;
    }

    /**
     * @return the vrPreco
     */
    public BigDecimal getVrPreco() {
        return vrPreco;
    }

    /**
     * @param vrPreco the vrPreco to set
     */
    public void setVrPreco(BigDecimal vrPreco) {
        this.vrPreco = vrPreco;
    }

    /**
     * @return the qtEfetiva
     */
    public XSSFCell getQtEfetiva() {
        return qtEfetiva;
    }

    /**
     * @param qtEfetiva the qtEfetiva to set
     */
    public void setQtEfetiva(XSSFCell qtEfetiva) {
        this.qtEfetiva = qtEfetiva;
    }

    /**
     * @return the qtDiasExtra
     */
    public XSSFCell getQtDiasExtra() {
        return qtDiasExtra;
    }

    /**
     * @param qtDiasExtra the qtDiasExtra to set
     */
    public void setQtDiasExtra(XSSFCell qtDiasExtra) {
        this.qtDiasExtra = qtDiasExtra;
    }

    /**
     * @return the qtDiasAusencia
     */
    public XSSFCell getQtDiasAusencia() {
        return qtDiasAusencia;
    }

    /**
     * @param qtDiasAusencia the qtDiasAusencia to set
     */
    public void setQtDiasAusencia(XSSFCell qtDiasAusencia) {
        this.qtDiasAusencia = qtDiasAusencia;
    }

    /**
     * @return the vrPrevistoTermo
     */
    public XSSFCell getVrPrevistoTermo() {
        return vrPrevistoTermo;
    }

    /**
     * @param vrPrevistoTermo the vrPrevistoTermo to set
     */
    public void setVrPrevistoTermo(XSSFCell vrPrevistoTermo) {
        this.vrPrevistoTermo = vrPrevistoTermo;
    }

    /**
     * @return the vrFns
     */
    public XSSFCell getVrFns() {
        return vrFns;
    }

    /**
     * @param vrFns the vrFns to set
     */
    public void setVrFns(XSSFCell vrFns) {
        this.vrFns = vrFns;
    }

    /**
     * @return the considerarFNS
     */
    public boolean isConsiderarFNS() {
        return considerarFNS;
    }

    /**
     * @param considerarFNS the considerarFNS to set
     */
    public void setConsiderarFNS(boolean considerarFNS) {
        this.considerarFNS = considerarFNS;
    }

    /**
     * @return the nuPerfil
     */
    public Long getNuPerfil() {
        return nuPerfil;
    }

    /**
     * @param nuPerfil the nuPerfil to set
     */
    public void setNuPerfil(Long nuPerfil) {
        this.nuPerfil = nuPerfil;
    }

    /**
     * @return the precosPerfil
     */
    public Map<String, BigDecimal> getPrecosPerfil() {
        if (precosPerfil == null) {
            precosPerfil = new HashedMap<String, BigDecimal>();
        }
        return precosPerfil;
    }

    /**
     * @param precosPerfil the precosPerfil to set
     */
    public void setPrecosPerfil(Map<String, BigDecimal> precosPerfil) {
        this.precosPerfil = precosPerfil;
    }

    public void addVrPrecoPerfil(Long nuPerfil, Date dtInicioVigencia, BigDecimal vrPreco) {
        getPrecosPerfil().put(nuPerfil + "#" + dtInicioVigencia, vrPreco);
    }

    public BigDecimal getVrPrecoPerfil(Long nuPerfil, Date dtInicioVigencia) {

        BigDecimal result = getPrecosPerfil().get(nuPerfil + "#" + dtInicioVigencia);

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * @return the qtEfetivaPerfil
     */
    public Map<String, BigDecimal> getQtEfetivaPerfil() {
        if (qtEfetivaPerfil == null) {
            qtEfetivaPerfil = new HashedMap<String, BigDecimal>();
        }
        return qtEfetivaPerfil;
    }

    /**
     * @param qtEfetivaPerfil the qtEfetivaPerfil to set
     */
    public void setQtEfetivaPerfil(Map<String, BigDecimal> qtEfetivaPerfil) {
        this.qtEfetivaPerfil = qtEfetivaPerfil;
    }

    public void addQtEfetivaPerfil(Long nuPerfil, Date dtInicioVigencia, BigDecimal cellQtEfetiva) {
        getQtEfetivaPerfil().put(nuPerfil + "#" + dtInicioVigencia, cellQtEfetiva);
    }

    public BigDecimal getQtEfetivaPerfil(Long nuPerfil, Date dtInicioVigencia) {

        BigDecimal result = getQtEfetivaPerfil().get(nuPerfil + "#" + dtInicioVigencia);

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * @return the vrAusencias
     */
    public XSSFCell getVrAusencias() {
        return vrAusencias;
    }

    /**
     * @return the vrExtras
     */
    public XSSFCell getVrExtras() {
        return vrExtras;
    }

    /**
     * @param vrExtras the vrExtras to set
     */
    public void setVrExtras(XSSFCell vrExtras) {
        this.vrExtras = vrExtras;
    }

    /**
     * @param vrAusencias the vrAusencias to set
     */
    public void setVrAusencias(XSSFCell vrAusencias) {
        this.vrAusencias = vrAusencias;
    }

    /**
     * @return the vrEquipe
     */
    public XSSFCell getVrEquipe() {
        return vrEquipe;
    }

    /**
     * @param vrEquipe the vrEquipe to set
     */
    public void setVrEquipe(XSSFCell vrEquipe) {
        this.vrEquipe = vrEquipe;
    }

    /**
     * @return the qtEfetivaTotal
     */
    public XSSFCell getQtEfetivaTotal() {
        return qtEfetivaTotal;
    }

    /**
     * @param qtEfetivaTotal the qtEfetivaTotal to set
     */
    public void setQtEfetivaTotal(XSSFCell qtEfetivaTotal) {
        this.qtEfetivaTotal = qtEfetivaTotal;
    }

    /**
     * @return the vrPago
     */
    public XSSFCell getVrPago() {
        return vrPago;
    }

    /**
     * @param vrPago the vrPago to set
     */
    public void setVrPago(XSSFCell vrPago) {
        this.vrPago = vrPago;
    }

    /**
     * 
     * @return
     */
    public List<VigenciaMembrosDTO> getVigencias() {
        if (vigencias == null) {
            vigencias = new ArrayList<VigenciaMembrosDTO>();
        }
        return vigencias;
    }

    /**
     * 
     * @param vigencias
     */
    public void setVigencias(List<VigenciaMembrosDTO> vigencias) {
        this.vigencias = vigencias;
    }

    /**
     * @return the qtDiasExtraPerfil
     */
    public Map<Long, BigDecimal> getQtDiasExtraPerfil() {
        if (qtDiasExtraPerfil == null) {
            qtDiasExtraPerfil = new HashMap<Long, BigDecimal>();
        }
        return qtDiasExtraPerfil;
    }

    /**
     * @param qtDiasExtraPerfil the qtDiasExtraPerfil to set
     */
    public void setQtDiasExtraPerfil(Map<Long, BigDecimal> qtDiasExtraPerfil) {
        this.qtDiasExtraPerfil = qtDiasExtraPerfil;
    }

    /**
     * @return the qtAusenciasPerfil
     */
    public Map<Long, BigDecimal> getQtAusenciasPerfil() {
        if (qtAusenciasPerfil == null) {
            qtAusenciasPerfil = new HashMap<Long, BigDecimal>();
        }
        return qtAusenciasPerfil;
    }

    /**
     * @param qtAusenciasPerfil the qtAusenciasPerfil to set
     */
    public void setQtAusenciasPerfil(Map<Long, BigDecimal> qtAusenciasPerfil) {
        this.qtAusenciasPerfil = qtAusenciasPerfil;
    }

    /**
     * 
     * @param nuPerfil
     * @param qtExtras
     */
    public void addQtDiasExtraPerfil(Long nuPerfil, BigDecimal qtExtras) {
        getQtDiasExtraPerfil().put(nuPerfil, qtExtras);
    }

    /**
     * 
     * @param nuPerfil
     * @return
     */
    public BigDecimal getQtDiasExtraPerfil(Long nuPerfil) {

        BigDecimal result = getQtDiasExtraPerfil().get(nuPerfil);

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * 
     * @param nuPerfil
     * @param qtAusencias
     */
    public void addQtDiasAusenciaPerfil(Long nuPerfil, BigDecimal qtAusencias) {
        getQtAusenciasPerfil().put(nuPerfil, qtAusencias);
    }

    /**
     * 
     * @param nuPerfil
     * @return
     */
    public BigDecimal getQtDiasAusenciaPerfil(Long nuPerfil) {

        BigDecimal result = getQtAusenciasPerfil().get(nuPerfil);

        if (result == null) {
            return BigDecimal.ZERO;
        }

        return result;
    }

    /**
     * @return the nuEquipe
     */
    public Long getNuEquipe() {
        return nuEquipe;
    }

    /**
     * @param nuEquipe the nuEquipe to set
     */
    public void setNuEquipe(Long nuEquipe) {
        this.nuEquipe = nuEquipe;
    }

    /**
     * @return the pcDescontoProporcional
     */
    public XSSFCell getPcDescontoProporcional() {
        return pcDescontoProporcional;
    }

    /**
     * @param pcDescontoProporcional the pcDescontoProporcional to set
     */
    public void setPcDescontoProporcional(XSSFCell pcDescontoProporcional) {
        this.pcDescontoProporcional = pcDescontoProporcional;
    }

    /**
     * @return the vrDescontoProporcional
     */
    public XSSFCell getVrDescontoProporcional() {
        return vrDescontoProporcional;
    }

    /**
     * @param vrDescontoProporcional the vrDescontoProporcional to set
     */
    public void setVrDescontoProporcional(XSSFCell vrDescontoProporcional) {
        this.vrDescontoProporcional = vrDescontoProporcional;
    }

    
}
