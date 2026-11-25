package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.CadastroUsuarioForm;
import com.pfc.thindesk.entity.PerfilUsuario;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registrar")
    public String formularioCadastro(Model model) {
        model.addAttribute("cadastro", new CadastroUsuarioForm());
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("cadastro") CadastroUsuarioForm cadastro, BindingResult result) {
        if (!result.hasErrors()) {
            try {
                usuarioService.cadastrar(cadastro, PerfilUsuario.USUARIO);
                return "redirect:/login?cadastro";
            } catch (RegraNegocioException ex) {
                Formularios.registrarErro(result, ex);
            }
        }
        return "registrar";
    }
}
