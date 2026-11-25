package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente novoCliente(@Valid @RequestBody Cliente cliente) {
        return clienteService.novoCliente(cliente);
    }

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    @GetMapping("/{id}")
    public Cliente buscarCliente(@PathVariable String id) {
        return clienteService.buscarPorId(id);
    }
}
