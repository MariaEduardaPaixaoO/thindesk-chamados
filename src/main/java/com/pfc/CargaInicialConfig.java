package com.pfc;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.entity.PerfilUsuario;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.repository.ClienteRepository;
import com.pfc.thindesk.repository.HorarioAtendimentoRepository;
import com.pfc.thindesk.repository.TipoChamadoRepository;
import com.pfc.thindesk.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cria o usuário de demonstração e, quando habilitado, uma carga inicial de
 * registros para que o sistema possa ser avaliado logo após o deploy.
 * Os dados só são inseridos em coleções vazias.
 */
@Configuration
public class CargaInicialConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicialConfig.class);

    private static final String TECNICO_JOAO = "João Silva";
    private static final String TECNICO_ANA = "Ana Souza";

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TipoChamadoRepository tipoChamadoRepository;
    @Autowired
    private ChamadoRepository chamadoRepository;
    @Autowired
    private HorarioAtendimentoRepository horarioAtendimentoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${thindesk.demo.username}")
    private String demoUsername;

    @Value("${thindesk.demo.password}")
    private String demoPassword;

    @Value("${thindesk.carga-inicial}")
    private boolean cargaInicial;

    @Override
    public void run(String... args) {
        criarUsuario("Usuário Demonstração", demoUsername);

        if (!cargaInicial) {
            return;
        }
        criarUsuario(TECNICO_JOAO, "joao.silva");
        criarUsuario(TECNICO_ANA, "ana.souza");
        if (tipoChamadoRepository.count() == 0) {
            criarTiposChamado();
        }
        if (clienteRepository.count() == 0) {
            criarClientes();
        }
        if (horarioAtendimentoRepository.count() == 0) {
            criarHorarios();
        }
        if (chamadoRepository.count() == 0) {
            criarChamados();
        }
    }

    private void criarUsuario(String nome, String username) {
        String login = username.trim().toLowerCase();
        if (usuarioRepository.existsByUsername(login)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setUsername(login);
        usuario.setSenha(passwordEncoder.encode(demoPassword));
        usuario.setPerfil(PerfilUsuario.TECNICO);
        usuario.setDataCadastro(LocalDateTime.now());
        usuarioRepository.save(usuario);
        log.info("Usuário '{}' criado", login);
    }

    private void criarTiposChamado() {
        List.of("Hardware", "Software", "Rede", "Acesso e Senhas", "Impressora")
                .forEach(nome -> tipoChamadoRepository.save(new TipoChamado(nome)));
    }

    private void criarClientes() {
        clienteRepository.save(cliente("Carla Mendes", "(11) 98765-4321", "Financeiro"));
        clienteRepository.save(cliente("Roberto Lima", "(11) 97654-3210", "Recursos Humanos"));
        clienteRepository.save(cliente("Fernanda Alves", "(11) 96543-2109", "Comercial"));
        clienteRepository.save(cliente("Paulo Teixeira", "(11) 95432-1098", "Logística"));
    }

    private void criarHorarios() {
        horarioAtendimentoRepository.save(new HorarioAtendimento(null, "Suporte Técnico", "Segunda-feira", "08:00", "18:00"));
        horarioAtendimentoRepository.save(new HorarioAtendimento(null, "Suporte Técnico", "Sexta-feira", "08:00", "17:00"));
        horarioAtendimentoRepository.save(new HorarioAtendimento(null, "Infraestrutura", "Sábado", "08:00", "12:00"));
    }

    private void criarChamados() {
        chamado("Computador não liga", "O computador da recepção não liga desde o início do expediente.",
                "Hardware", "Carla Mendes", TECNICO_JOAO, StatusChamado.CONCLUIDO, 24,
                "Fonte de alimentação substituída e equipamento testado.");
        chamado("Sem acesso à internet no setor", "Os computadores do setor de RH estão sem acesso à internet desde ontem à tarde.",
                "Rede", "Roberto Lima", TECNICO_ANA, StatusChamado.CONCLUIDO, 20,
                "Switch do setor reiniciado e cabo de uplink substituído.");
        chamado("Impressora com atolamento de papel", "A impressora do segundo andar trava o papel em todas as impressões.",
                "Impressora", "Fernanda Alves", TECNICO_JOAO, StatusChamado.CONCLUIDO, 15,
                "Rolete de tração limpo e bandeja de papel ajustada.");
        chamado("Redefinição de senha do ERP", "Usuário bloqueado no ERP após várias tentativas de acesso.",
                "Acesso e Senhas", "Paulo Teixeira", TECNICO_ANA, StatusChamado.CONCLUIDO, 11,
                "Senha redefinida e usuário orientado sobre a política de senhas.");
        chamado("Troca de teclado", "Teclado com várias teclas sem funcionar na estação do financeiro.",
                "Hardware", "Carla Mendes", null, StatusChamado.CANCELADO, 9, null);
        chamado("Instalação do pacote Office", "Necessária a instalação do pacote Office no notebook novo do setor comercial.",
                "Software", "Fernanda Alves", TECNICO_JOAO, StatusChamado.EM_ANDAMENTO, 6, null);
        chamado("Lentidão no sistema financeiro", "O sistema financeiro demora mais de um minuto para gerar relatórios.",
                "Software", "Carla Mendes", TECNICO_ANA, StatusChamado.EM_ANDAMENTO, 4, null);
        chamado("Monitor com falha de imagem", "O monitor apresenta linhas horizontais e piscadas constantes.",
                "Hardware", "Roberto Lima", null, StatusChamado.ABERTO, 2, null);
        chamado("Acesso à pasta compartilhada", "Solicito acesso de leitura à pasta compartilhada da logística.",
                "Acesso e Senhas", "Paulo Teixeira", null, StatusChamado.ABERTO, 1, null);
        chamado("Wi-Fi instável na sala de reuniões", "A conexão sem fio cai várias vezes durante as reuniões.",
                "Rede", "Fernanda Alves", null, StatusChamado.ABERTO, 0, null);
        log.info("Carga inicial de chamados concluída");
    }

    private Cliente cliente(String nome, String telefone, String setor) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        cliente.setSetor(setor);
        return cliente;
    }

    private void chamado(String titulo, String descricao, String tipo, String cliente, String tecnico,
                         StatusChamado status, int diasAtras, String solucao) {
        LocalDateTime abertura = LocalDateTime.now().minusDays(diasAtras).minusHours(1);
        Chamado chamado = new Chamado();
        chamado.setTitulo(titulo);
        chamado.setDescricao(descricao);
        chamado.setTipo(tipo);
        chamado.setUsuario(cliente);
        chamado.setTecnico(tecnico);
        chamado.setStatus(status);
        chamado.setSolucao(solucao);
        chamado.setAbertoPor(demoUsername.trim().toLowerCase());
        chamado.setDataAbertura(abertura);
        chamado.setDataAtualizacao(status == StatusChamado.ABERTO ? abertura : abertura.plusMinutes(50));
        if (chamado.isEncerrado()) {
            chamado.setDataConclusao(abertura.plusMinutes(50));
        }
        chamadoRepository.save(chamado);
    }
}
