package com.pfc.thindesk.service;

import com.pfc.thindesk.dto.CadastroUsuarioForm;
import com.pfc.thindesk.entity.PerfilUsuario;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario cadastrar(CadastroUsuarioForm cadastro, PerfilUsuario perfil) {
        String username = cadastro.getUsername().trim().toLowerCase();
        if (usuarioRepository.existsByUsername(username)) {
            throw new RegraNegocioException("username", "Este nome de usuário já está em uso");
        }
        if (!cadastro.getSenha().equals(cadastro.getConfirmacaoSenha())) {
            throw new RegraNegocioException("confirmacaoSenha", "As senhas não conferem");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(cadastro.getNome().trim());
        usuario.setUsername(username);
        usuario.setSenha(passwordEncoder.encode(cadastro.getSenha()));
        usuario.setPerfil(perfil);
        usuario.setDataCadastro(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTecnicos() {
        return usuarioRepository.findByPerfilOrderByNomeAsc(PerfilUsuario.TECNICO);
    }
}
