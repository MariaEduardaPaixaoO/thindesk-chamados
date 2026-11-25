package com.pfc.thindesk.exception;

import com.pfc.thindesk.entity.StatusChamado;

public class ChamadoEncerradoException extends RegraNegocioException {

    public ChamadoEncerradoException(StatusChamado status) {
        super("O chamado já está " + status.getDescricao().toLowerCase() + " e não pode ser alterado");
    }
}
