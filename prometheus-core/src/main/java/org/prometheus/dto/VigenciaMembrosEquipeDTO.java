package org.prometheus.dto;

public class VigenciaMembrosEquipeDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private String coMatricula;
    private String noMembro;
    private Long nuPosicaoMembro;
    private java.util.Date dtInicioMembro;
    private java.util.Date dtFimMembro;

    public void setCoMatricula(String coMatricula){
        this.coMatricula = coMatricula;
    }

    public String getCoMatricula(){
        return this.coMatricula;
    }

    public void setNoMembro(String noMembro){
        this.noMembro = noMembro;
    }

    public String getNoMembro(){
        return this.noMembro;
    }

    public void setNuPosicaoMembro(Long nuPosicaoMembro){
        this.nuPosicaoMembro = nuPosicaoMembro;
    }

    public Long getNuPosicaoMembro(){
        return this.nuPosicaoMembro;
    }

    public void setDtInicioMembro(java.util.Date dtInicioMembro){
        this.dtInicioMembro = dtInicioMembro;
    }

    public java.util.Date getDtInicioMembro(){
        return this.dtInicioMembro;
    }

    public void setDtFimMembro(java.util.Date dtFimMembro){
        this.dtFimMembro = dtFimMembro;
    }

    public java.util.Date getDtFimMembro(){
        return this.dtFimMembro;
    }

}
