package com.pfc.thindesk.entity;

public enum PerfilUsuario {

    USUARIO("Usuário"),
    TECNICO("Técnico");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
