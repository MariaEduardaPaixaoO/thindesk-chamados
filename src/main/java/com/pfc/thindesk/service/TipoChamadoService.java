package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.exception.RecursoNaoEncontradoException;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.repository.TipoChamadoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TipoChamadoService {

    @Autowired
    private TipoChamadoRepository tipoChamadoRepository;

    @Autowired
    private ChamadoRepository chamadoRepository;

    public List<TipoChamado> listarTipos() {
        return tipoChamadoRepository.findAll(Sort.by("nome"));
    }

    public TipoChamado salvar(TipoChamado tipoChamado) {
        String nome = tipoChamado.getNome().trim();
        boolean existe = listarTipos().stream().anyMatch(t -> t.getNome().equalsIgnoreCase(nome));
        if (existe) {
            throw new RegraNegocioException("nome", "Já existe um tipo de chamado com este nome");
        }
        tipoChamado.setId(null);
        tipoChamado.setNome(nome);
        return tipoChamadoRepository.save(tipoChamado);
    }

    public void excluir(String id) {
        TipoChamado tipoChamado = tipoChamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de chamado não encontrado com id: " + id));
        if (chamadoRepository.existsByTipo(tipoChamado.getNome())) {
            throw new RegraNegocioException("Não é possível excluir um tipo que já foi usado em chamados");
        }
        tipoChamadoRepository.deleteById(id);
    }
}
