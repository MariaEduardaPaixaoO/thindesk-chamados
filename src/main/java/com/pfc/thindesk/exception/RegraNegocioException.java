package com.pfc.thindesk.exception;

/**
 * Violação de uma regra de negócio. Quando a regra se refere a um campo
 * do formulário, o nome do campo é informado para exibir a mensagem junto a ele.
 */
public class RegraNegocioException extends RuntimeException {

    private final String campo;

    public RegraNegocioException(String mensagem) {
        this(null, mensagem);
    }

    public RegraNegocioException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
