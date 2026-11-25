package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.exception.ChamadoEncerradoException;
import com.pfc.thindesk.exception.RecursoNaoEncontradoException;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ChamadoService {

    private static final int TAMANHO_MINIMO_SOLUCAO = 10;

    @Autowired
    private ChamadoRepository chamadoRepository;

    /** Abre o chamado com status ABERTO e data de abertura definidos pelo sistema. */
    public Chamado criarChamado(Chamado chamado, String abertoPor) {
        LocalDateTime agora = LocalDateTime.now();
        chamado.setId(null);
        chamado.setTitulo(limpar(chamado.getTitulo()));
        chamado.setDescricao(limpar(chamado.getDescricao()));
        chamado.setTipo(limpar(chamado.getTipo()));
        chamado.setUsuario(limpar(chamado.getUsuario()));
        chamado.setTecnico(limpar(chamado.getTecnico()));
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setSolucao(null);
        chamado.setAbertoPor(abertoPor);
        chamado.setDataAbertura(agora);
        chamado.setDataAtualizacao(agora);
        chamado.setDataConclusao(null);
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamados() {
        return chamadoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataAbertura"));
    }

    public List<Chamado> listarPorStatus(StatusChamado status) {
        if (status == null) {
            return listarChamados();
        }
        return chamadoRepository.findByStatusOrderByDataAberturaDesc(status);
    }

    public Chamado buscarPorId(String id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado com id: " + id));
    }

    public Chamado atualizarChamado(String id, Chamado chamadoAtualizado) {
        Chamado chamado = buscarPorId(id);
        if (chamado.isEncerrado()) {
            throw new ChamadoEncerradoException(chamado.getStatus());
        }

        chamado.setTitulo(limpar(chamadoAtualizado.getTitulo()));
        chamado.setDescricao(limpar(chamadoAtualizado.getDescricao()));
        chamado.setTipo(limpar(chamadoAtualizado.getTipo()));
        chamado.setUsuario(limpar(chamadoAtualizado.getUsuario()));
        chamado.setTecnico(limpar(chamadoAtualizado.getTecnico()));
        chamado.setSolucao(limpar(chamadoAtualizado.getSolucao()));

        StatusChamado novoStatus = chamadoAtualizado.getStatus() != null
                ? chamadoAtualizado.getStatus()
                : chamado.getStatus();

        // Ao receber um técnico responsável, o chamado entra em atendimento
        if (novoStatus == StatusChamado.ABERTO && chamado.getTecnico() != null) {
            novoStatus = StatusChamado.EM_ANDAMENTO;
        }
        if (novoStatus == StatusChamado.CONCLUIDO) {
            validarConclusao(chamado);
        }

        LocalDateTime agora = LocalDateTime.now();
        chamado.setStatus(novoStatus);
        chamado.setDataAtualizacao(agora);
        if (chamado.isEncerrado()) {
            chamado.setDataConclusao(agora);
        }
        return chamadoRepository.save(chamado);
    }

    public Chamado cancelarChamado(String id) {
        Chamado chamado = buscarPorId(id);
        if (chamado.isEncerrado()) {
            throw new ChamadoEncerradoException(chamado.getStatus());
        }
        LocalDateTime agora = LocalDateTime.now();
        chamado.setStatus(StatusChamado.CANCELADO);
        chamado.setDataAtualizacao(agora);
        chamado.setDataConclusao(agora);
        return chamadoRepository.save(chamado);
    }

    private void validarConclusao(Chamado chamado) {
        if (chamado.getTecnico() == null) {
            throw new RegraNegocioException("tecnico", "Informe o técnico responsável para concluir o chamado");
        }
        if (chamado.getSolucao() == null || chamado.getSolucao().length() < TAMANHO_MINIMO_SOLUCAO) {
            throw new RegraNegocioException("solucao",
                    "Descreva a solução com pelo menos " + TAMANHO_MINIMO_SOLUCAO + " caracteres para concluir o chamado");
        }
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

}
