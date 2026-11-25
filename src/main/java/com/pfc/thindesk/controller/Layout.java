package com.pfc.thindesk.controller;

import org.springframework.ui.Model;

/** Monta as páginas internas dentro do layout com a sidebar. */
final class Layout {

    private static final String VIEW = "layout";

    private Layout() {
    }

    /**
     * @param template nome do template que contém o fragmento "content"
     * @param titulo   título exibido na aba do navegador
     */
    static String pagina(Model model, String template, String titulo) {
        model.addAttribute("content", template + " :: content");
        model.addAttribute("tituloPagina", titulo);
        return VIEW;
    }
}
