package com.pfc.thindesk.controller;

import com.pfc.thindesk.exception.RegraNegocioException;
import org.springframework.validation.BindingResult;

/** Exibe no formulário os erros de regra de negócio retornados pelos services. */
final class Formularios {

    private Formularios() {
    }

    static void registrarErro(BindingResult result, RegraNegocioException ex) {
        if (ex.getCampo() != null) {
            result.rejectValue(ex.getCampo(), "regraNegocio", ex.getMessage());
        } else {
            result.reject("regraNegocio", ex.getMessage());
        }
    }
}
