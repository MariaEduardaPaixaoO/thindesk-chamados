package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.exception.ChamadoEncerradoException;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.ClienteService;
import com.pfc.thindesk.service.TipoChamadoService;
import com.pfc.thindesk.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chamados")
public class ChamadoWebController {

    /** Status que podem ser escolhidos na edição; o cancelamento tem ação própria. */
    private static final List<StatusChamado> STATUS_EDICAO =
            List.of(StatusChamado.ABERTO, StatusChamado.EM_ANDAMENTO, StatusChamado.CONCLUIDO);

    @Autowired
    private ChamadoService chamadoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private TipoChamadoService tipoChamadoService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String chamados(@RequestParam(required = false) StatusChamado status, Model model) {
        model.addAttribute("chamados", chamadoService.listarPorStatus(status));
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("statusDisponiveis", StatusChamado.values());
        return Layout.pagina(model, "chamados", "Chamados");
    }

    @GetMapping("/novo")
    public String novoChamado(Model model) {
        model.addAttribute("chamado", new Chamado());
        return paginaCadastro(model);
    }

    @PostMapping("/novo")
    public String criarChamado(@Valid @ModelAttribute("chamado") Chamado chamado, BindingResult result,
                               Principal principal, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return paginaCadastro(model);
        }
        Chamado criado = chamadoService.criarChamado(chamado, principal.getName());
        redirect.addFlashAttribute("sucesso", "Chamado aberto com sucesso.");
        return "redirect:/chamados/" + criado.getId();
    }

    @GetMapping("/{id}")
    public String detalhesChamado(@PathVariable String id, Model model) {
        model.addAttribute("chamado", chamadoService.buscarPorId(id));
        return Layout.pagina(model, "chamados/detalhes", "Detalhes do chamado");
    }

    @GetMapping("/atualizar/{id}")
    public String editarChamado(@PathVariable String id, Model model, RedirectAttributes redirect) {
        Chamado chamado = chamadoService.buscarPorId(id);
        if (chamado.isEncerrado()) {
            redirect.addFlashAttribute("erro", "Chamados concluídos ou cancelados não podem ser editados.");
            return "redirect:/chamados/" + id;
        }
        model.addAttribute("chamado", chamado);
        return paginaEdicao(id, model);
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarChamado(@PathVariable String id, @Valid @ModelAttribute("chamado") Chamado chamadoAtualizado,
                                   BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                chamadoService.atualizarChamado(id, chamadoAtualizado);
                redirect.addFlashAttribute("sucesso", "Chamado atualizado com sucesso.");
                return "redirect:/chamados/" + id;
            } catch (ChamadoEncerradoException ex) {
                redirect.addFlashAttribute("erro", ex.getMessage());
                return "redirect:/chamados/" + id;
            } catch (RegraNegocioException ex) {
                Formularios.registrarErro(result, ex);
            }
        }
        return paginaEdicao(id, model);
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarChamado(@PathVariable String id, RedirectAttributes redirect) {
        try {
            chamadoService.cancelarChamado(id);
            redirect.addFlashAttribute("sucesso", "Chamado cancelado.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/chamados/" + id;
    }

    private String paginaCadastro(Model model) {
        carregarOpcoes(model);
        return Layout.pagina(model, "chamados/novo", "Novo chamado");
    }

    private String paginaEdicao(String id, Model model) {
        model.addAttribute("chamadoId", id);
        model.addAttribute("statusEdicao", STATUS_EDICAO);
        carregarOpcoes(model);
        return Layout.pagina(model, "chamados/atualizar", "Editar chamado");
    }

    private void carregarOpcoes(Model model) {
        model.addAttribute("tipos", tipoChamadoService.listarTipos());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("tecnicos", usuarioService.listarTecnicos());
    }
}
