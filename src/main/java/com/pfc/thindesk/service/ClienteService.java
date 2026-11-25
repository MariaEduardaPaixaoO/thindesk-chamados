package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.exception.RecursoNaoEncontradoException;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.repository.ClienteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Cliente novoCliente(Cliente cliente) {
        cliente.setId(null);
        normalizar(cliente);
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll(Sort.by("nome"));
    }

    public Cliente buscarPorId(String id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com id: " + id));
    }

    public Cliente atualizarCliente(String id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id);
        String nomeAnterior = cliente.getNome();

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setSetor(clienteAtualizado.getSetor());
        normalizar(cliente);
        Cliente salvo = clienteRepository.save(cliente);

        // Mantém os chamados do cliente vinculados ao novo nome
        if (nomeAnterior != null && !nomeAnterior.equals(salvo.getNome())) {
            mongoTemplate.updateMulti(
                    new Query(Criteria.where("usuario").is(nomeAnterior)),
                    new Update().set("usuario", salvo.getNome()),
                    Chamado.class);
        }
        return salvo;
    }

    public void excluirCliente(String id) {
        Cliente cliente = buscarPorId(id);
        if (chamadoRepository.existsByUsuario(cliente.getNome())) {
            throw new RegraNegocioException("Não é possível excluir um cliente que possui chamados registrados");
        }
        clienteRepository.deleteById(id);
    }

    private void normalizar(Cliente cliente) {
        cliente.setNome(cliente.getNome().trim());
        cliente.setTelefone(cliente.getTelefone().trim());
        cliente.setSetor(cliente.getSetor().trim());
    }
}
