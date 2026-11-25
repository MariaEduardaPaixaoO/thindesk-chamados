package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.service.TipoChamadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tipo-chamado")
public class TipoChamadoController {

    @Autowired
    private TipoChamadoService tipoChamadoService;

    @GetMapping
    public String listarTipos(Model model) {
        model.addAttribute("tipoChamado", new TipoChamado());
        return pagina(model);
    }

    @PostMapping("/salvar")
    public String salvarTipo(@Valid @ModelAttribute("tipoChamado") TipoChamado tipoChamado, BindingResult result,
                             Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                tipoChamadoService.salvar(tipoChamado);
                redirect.addFlashAttribute("sucesso", "Tipo de chamado cadastrado.");
                return "redirect:/tipo-chamado";
            } catch (RegraNegocioException ex) {
                Formularios.registrarErro(result, ex);
            }
        }
        return pagina(model);
    }

    @PostMapping("/deletar/{id}")
    public String excluirTipo(@PathVariable String id, RedirectAttributes redirect) {
        try {
            tipoChamadoService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Tipo de chamado excluído.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/tipo-chamado";
    }

    private String pagina(Model model) {
        model.addAttribute("tipos", tipoChamadoService.listarTipos());
        return Layout.pagina(model, "tipo-chamado", "Tipos de chamado");
    }
}
