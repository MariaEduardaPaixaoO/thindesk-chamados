package com.pfc.thindesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "chamados")
public class Chamado{

    @Id
    private String id;

    @NotBlank(message = "Informe o título do chamado")
    @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
    private String titulo;

    @NotBlank(message = "Descreva o problema")
    @Size(min = 10, max = 2000, message = "A descrição deve ter entre 10 e 2000 caracteres")
    private String descricao;

    private StatusChamado status;

    @NotBlank(message = "Selecione o tipo do chamado")
    private String tipo;

    /** Técnico responsável pelo atendimento. */
    private String tecnico;

    /** Cliente que solicitou o atendimento. */
    @NotBlank(message = "Selecione o solicitante")
    private String usuario;

    @Size(max = 2000, message = "A solução deve ter no máximo 2000 caracteres")
    private String solucao;

    /** Usuário do sistema que registrou o chamado. */
    private String abertoPor;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataConclusao;

    /** Chamados concluídos ou cancelados não podem mais ser alterados. */
    @JsonIgnore
    public boolean isEncerrado() {
        return status == StatusChamado.CONCLUIDO || status == StatusChamado.CANCELADO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusChamado getStatus() {
        return status;
    }

    public void setStatus(StatusChamado status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public String getAbertoPor() {
        return abertoPor;
    }

    public void setAbertoPor(String abertoPor) {
        this.abertoPor = abertoPor;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

}
