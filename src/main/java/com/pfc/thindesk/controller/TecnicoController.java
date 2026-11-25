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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tecnicos")
public class TecnicoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarTecnicos(Model model) {
        model.addAttribute("cadastro", new CadastroUsuarioForm());
        return pagina(model);
    }

    @PostMapping("/salvar")
    public String salvarTecnico(@Valid @ModelAttribute("cadastro") CadastroUsuarioForm cadastro, BindingResult result,
                                Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                usuarioService.cadastrar(cadastro, PerfilUsuario.TECNICO);
                redirect.addFlashAttribute("sucesso", "Técnico cadastrado com sucesso.");
                return "redirect:/tecnicos";
            } catch (RegraNegocioException ex) {
                Formularios.registrarErro(result, ex);
            }
        }
        return pagina(model);
    }

    private String pagina(Model model) {
        model.addAttribute("tecnicos", usuarioService.listarTecnicos());
        return Layout.pagina(model, "tecnicos", "Técnicos");
    }
}
