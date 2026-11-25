package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.service.ClienteService;
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
@RequestMapping("/clientes")
public class ClienteWebController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return Layout.pagina(model, "clientes", "Clientes");
    }

    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return paginaFormulario(model, "/clientes/novo", "Novo cliente");
    }

    @PostMapping("/novo")
    public String criarCliente(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result,
                               Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return paginaFormulario(model, "/clientes/novo", "Novo cliente");
        }
        clienteService.novoCliente(cliente);
        redirect.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso.");
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable String id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return paginaFormulario(model, "/clientes/editar/" + id, "Editar cliente");
    }

    @PostMapping("/editar/{id}")
    public String atualizarCliente(@PathVariable String id, @Valid @ModelAttribute("cliente") Cliente cliente,
                                   BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return paginaFormulario(model, "/clientes/editar/" + id, "Editar cliente");
        }
        clienteService.atualizarCliente(id, cliente);
        redirect.addFlashAttribute("sucesso", "Cliente atualizado com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/deletar/{id}")
    public String excluirCliente(@PathVariable String id, RedirectAttributes redirect) {
        try {
            clienteService.excluirCliente(id);
            redirect.addFlashAttribute("sucesso", "Cliente excluído.");
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/clientes";
    }

    private String paginaFormulario(Model model, String acao, String titulo) {
        model.addAttribute("acao", acao);
        model.addAttribute("tituloFormulario", titulo);
        return Layout.pagina(model, "clientes/form", titulo);
    }
}
