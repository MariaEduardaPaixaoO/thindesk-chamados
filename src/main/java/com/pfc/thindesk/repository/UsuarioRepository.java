package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.PerfilUsuario;
import com.pfc.thindesk.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findByPerfilOrderByNomeAsc(PerfilUsuario perfil);
}
