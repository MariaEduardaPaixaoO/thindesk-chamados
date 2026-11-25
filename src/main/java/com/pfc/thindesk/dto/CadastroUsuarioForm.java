package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados do formulário de cadastro de usuários e técnicos. */
public class CadastroUsuarioForm {

    @NotBlank(message = "Informe o nome")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Informe o nome de usuário")
    @Size(min = 3, max = 30, message = "O nome de usuário deve ter entre 3 e 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Use apenas letras, números, ponto, hífen ou sublinhado")
    private String username;

    @NotBlank(message = "Informe a senha")
    @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres")
    private String senha;

    @NotBlank(message = "Confirme a senha")
    private String confirmacaoSenha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }
}
