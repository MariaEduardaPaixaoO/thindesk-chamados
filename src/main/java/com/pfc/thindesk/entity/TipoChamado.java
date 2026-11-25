package com.pfc.thindesk.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tiposChamado")
public class TipoChamado {

    @Id
    private String id;

    @NotBlank(message = "Informe o nome do tipo")
    @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres")
    private String nome;

    public TipoChamado() {
    }

    public TipoChamado(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
