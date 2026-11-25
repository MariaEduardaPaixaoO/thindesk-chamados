package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/ajustes-horarios")
public class HorarioAtendimentoController {

    private static final List<String> DIAS_SEMANA = List.of(
            "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira",
            "Sexta-feira", "Sábado", "Domingo");

    @Autowired
    private HorarioAtendimentoService horarioAtendimentoService;

    @GetMapping
    public String listarHorarios(Model model) {
        model.addAttribute("novoHorario", new HorarioAtendimento());
        return pagina(model);
    }

    @PostMapping("/salvar")
    public String salvarHorario(@Valid @ModelAttribute("novoHorario") HorarioAtendimento horarioAtendimento,
                                BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                horarioAtendimentoService.salvar(horarioAtendimento);
                redirect.addFlashAttribute("sucesso", "Horário salvo com sucesso.");
                return "redirect:/ajustes-horarios";
            } catch (RegraNegocioException ex) {
                Formularios.registrarErro(result, ex);
            }
        }
        return pagina(model);
    }

    @PostMapping("/deletar/{id}")
    public String deletarHorario(@PathVariable String id, RedirectAttributes redirect) {
        horarioAtendimentoService.deletar(id);
        redirect.addFlashAttribute("sucesso", "Horário removido.");
        return "redirect:/ajustes-horarios";
    }

    private String pagina(Model model) {
        model.addAttribute("horarios", horarioAtendimentoService.listarTodos());
        model.addAttribute("diasSemana", DIAS_SEMANA);
        return Layout.pagina(model, "ajustes-horarios", "Horário de atendimento");
    }
}
