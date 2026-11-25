package com.pfc.thindesk.dto;

import com.pfc.thindesk.entity.Chamado;
import java.util.List;
import java.util.Map;

/** Indicadores exibidos no dashboard, calculados a partir dos chamados cadastrados. */
public class IndicadoresDashboard {

    private long total;
    private long abertos;
    private long emAndamento;
    private long concluidos;
    private long cancelados;

    private Map<String, Long> porStatus;
    private Map<String, Long> porTipo;
    private Map<String, Long> porTecnico;

    private List<String> diasRotulos;
    private List<Long> diasTotais;

    private List<Chamado> ultimosChamados;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getAbertos() {
        return abertos;
    }

    public void setAbertos(long abertos) {
        this.abertos = abertos;
    }

    public long getEmAndamento() {
        return emAndamento;
    }

    public void setEmAndamento(long emAndamento) {
        this.emAndamento = emAndamento;
    }

    public long getConcluidos() {
        return concluidos;
    }

    public void setConcluidos(long concluidos) {
        this.concluidos = concluidos;
    }

    public long getCancelados() {
        return cancelados;
    }

    public void setCancelados(long cancelados) {
        this.cancelados = cancelados;
    }

    public Map<String, Long> getPorStatus() {
        return porStatus;
    }

    public void setPorStatus(Map<String, Long> porStatus) {
        this.porStatus = porStatus;
    }

    public Map<String, Long> getPorTipo() {
        return porTipo;
    }

    public void setPorTipo(Map<String, Long> porTipo) {
        this.porTipo = porTipo;
    }

    public Map<String, Long> getPorTecnico() {
        return porTecnico;
    }

    public void setPorTecnico(Map<String, Long> porTecnico) {
        this.porTecnico = porTecnico;
    }

    public List<String> getDiasRotulos() {
        return diasRotulos;
    }

    public void setDiasRotulos(List<String> diasRotulos) {
        this.diasRotulos = diasRotulos;
    }

    public List<Long> getDiasTotais() {
        return diasTotais;
    }

    public void setDiasTotais(List<Long> diasTotais) {
        this.diasTotais = diasTotais;
    }

    public List<Chamado> getUltimosChamados() {
        return ultimosChamados;
    }

    public void setUltimosChamados(List<Chamado> ultimosChamados) {
        this.ultimosChamados = ultimosChamados;
    }
}
