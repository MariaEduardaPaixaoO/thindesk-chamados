package com.pfc.thindesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.pfc.thindesk.dto.CadastroUsuarioForm;
import com.pfc.thindesk.entity.PerfilUsuario;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void cadastrarArmazenaSenhaCriptografada() {
        when(usuarioRepository.existsByUsername("maria")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario usuario = usuarioService.cadastrar(formulario("Maria", "senha123", "senha123"), PerfilUsuario.USUARIO);

        assertEquals("maria", usuario.getUsername());
        assertEquals(PerfilUsuario.USUARIO, usuario.getPerfil());
        assertNotEquals("senha123", usuario.getSenha());
        assertTrue(passwordEncoder.matches("senha123", usuario.getSenha()));
    }

    @Test
    void naoPermiteUsuarioRepetido() {
        when(usuarioRepository.existsByUsername("maria")).thenReturn(true);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar(formulario("maria", "senha123", "senha123"), PerfilUsuario.USUARIO));

        assertEquals("username", ex.getCampo());
    }

    @Test
    void senhasDevemConferir() {
        when(usuarioRepository.existsByUsername("maria")).thenReturn(false);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> usuarioService.cadastrar(formulario("maria", "senha123", "outra123"), PerfilUsuario.USUARIO));

        assertEquals("confirmacaoSenha", ex.getCampo());
    }

    private CadastroUsuarioForm formulario(String username, String senha, String confirmacao) {
        CadastroUsuarioForm form = new CadastroUsuarioForm();
        form.setNome("Maria Santos");
        form.setUsername(username);
        form.setSenha(senha);
        form.setConfirmacaoSenha(confirmacao);
        return form;
    }
}
