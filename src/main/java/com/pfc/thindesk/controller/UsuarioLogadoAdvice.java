package com.pfc.thindesk.controller;

import java.security.Principal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Disponibiliza o usuário autenticado para a sidebar de todas as páginas. */
@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("usuarioLogado")
    public String usuarioLogado(Principal principal) {
        return principal != null ? principal.getName() : null;
    }
}
