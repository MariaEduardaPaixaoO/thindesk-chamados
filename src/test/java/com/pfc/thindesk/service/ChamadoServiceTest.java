package com.pfc.thindesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.exception.ChamadoEncerradoException;
import com.pfc.thindesk.exception.RecursoNaoEncontradoException;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @InjectMocks
    private ChamadoService chamadoService;

    @BeforeEach
    void setUp() {
        lenient().when(chamadoRepository.save(any(Chamado.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void criarChamadoSempreIniciaComoAberto() {
        Chamado chamado = dadosChamado();
        chamado.setStatus(StatusChamado.CONCLUIDO);
        chamado.setSolucao("Solução enviada junto com a abertura");

        Chamado criado = chamadoService.criarChamado(chamado, "demo");

        assertEquals(StatusChamado.ABERTO, criado.getStatus());
        assertNull(criado.getSolucao());
        assertNull(criado.getDataConclusao());
        assertNotNull(criado.getDataAbertura());
        assertEquals("demo", criado.getAbertoPor());
    }

    @Test
    void atribuirTecnicoColocaChamadoEmAndamento() {
        chamadoExistente(StatusChamado.ABERTO);
        Chamado dados = dadosChamado();
        dados.setStatus(StatusChamado.ABERTO);
        dados.setTecnico("João Silva");

        Chamado atualizado = chamadoService.atualizarChamado("1", dados);

        assertEquals(StatusChamado.EM_ANDAMENTO, atualizado.getStatus());
        assertEquals("João Silva", atualizado.getTecnico());
    }

    @Test
    void concluirExigeSolucao() {
        chamadoExistente(StatusChamado.EM_ANDAMENTO);
        Chamado dados = dadosChamado();
        dados.setStatus(StatusChamado.CONCLUIDO);
        dados.setTecnico("João Silva");

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> chamadoService.atualizarChamado("1", dados));

        assertEquals("solucao", ex.getCampo());
        verify(chamadoRepository, never()).save(any());
    }

    @Test
    void concluirExigeTecnicoResponsavel() {
        chamadoExistente(StatusChamado.ABERTO);
        Chamado dados = dadosChamado();
        dados.setStatus(StatusChamado.CONCLUIDO);
        dados.setSolucao("Cabo de energia substituído");

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> chamadoService.atualizarChamado("1", dados));

        assertEquals("tecnico", ex.getCampo());
    }

    @Test
    void concluirRegistraSolucaoEDataDeEncerramento() {
        chamadoExistente(StatusChamado.EM_ANDAMENTO);
        Chamado dados = dadosChamado();
        dados.setStatus(StatusChamado.CONCLUIDO);
        dados.setTecnico("João Silva");
        dados.setSolucao("Cabo de energia substituído");

        Chamado concluido = chamadoService.atualizarChamado("1", dados);

        assertEquals(StatusChamado.CONCLUIDO, concluido.getStatus());
        assertEquals("Cabo de energia substituído", concluido.getSolucao());
        assertNotNull(concluido.getDataConclusao());
    }

    @Test
    void naoPermiteAlterarChamadoConcluido() {
        chamadoExistente(StatusChamado.CONCLUIDO);

        assertThrows(ChamadoEncerradoException.class,
                () -> chamadoService.atualizarChamado("1", dadosChamado()));
        verify(chamadoRepository, never()).save(any());
    }

    @Test
    void cancelarChamadoAberto() {
        chamadoExistente(StatusChamado.ABERTO);

        Chamado cancelado = chamadoService.cancelarChamado("1");

        assertEquals(StatusChamado.CANCELADO, cancelado.getStatus());
        assertNotNull(cancelado.getDataConclusao());
    }

    @Test
    void naoPermiteCancelarChamadoJaCancelado() {
        chamadoExistente(StatusChamado.CANCELADO);

        assertThrows(ChamadoEncerradoException.class, () -> chamadoService.cancelarChamado("1"));
    }

    @Test
    void chamadoInexistenteLancaExcecao() {
        when(chamadoRepository.findById("nao-existe")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> chamadoService.buscarPorId("nao-existe"));
    }

    private Chamado dadosChamado() {
        Chamado chamado = new Chamado();
        chamado.setTitulo("Impressora não liga");
        chamado.setDescricao("A impressora do segundo andar não liga desde ontem");
        chamado.setTipo("Impressora");
        chamado.setUsuario("Carla Mendes");
        return chamado;
    }

    private void chamadoExistente(StatusChamado status) {
        Chamado chamado = dadosChamado();
        chamado.setId("1");
        chamado.setStatus(status);
        chamado.setDataAbertura(LocalDateTime.now().minusDays(1));
        when(chamadoRepository.findById("1")).thenReturn(Optional.of(chamado));
    }
}
