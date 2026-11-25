package com.pfc.thindesk.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;

    @NotBlank(message = "Informe o nome do cliente")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Informe o telefone")
    @Pattern(regexp = "^[0-9()+\\-\\s]{8,20}$", message = "Informe um telefone válido, ex.: (11) 98765-4321")
    private String telefone;

    @NotBlank(message = "Informe o setor")
    @Size(max = 60, message = "O setor deve ter no máximo 60 caracteres")
    private String setor;

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

}
