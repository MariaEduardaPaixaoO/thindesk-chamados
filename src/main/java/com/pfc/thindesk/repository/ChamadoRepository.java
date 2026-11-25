package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.StatusChamado;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChamadoRepository extends MongoRepository<Chamado, String> {
    List<Chamado> findByStatus(StatusChamado status);

    List<Chamado> findByStatusOrderByDataAberturaDesc(StatusChamado status);

    List<Chamado> findTop5ByOrderByDataAberturaDesc();

    List<Chamado> findByDataAberturaGreaterThanEqual(LocalDateTime inicio);

    long countByStatus(StatusChamado status);

    boolean existsByUsuario(String usuario);

    boolean existsByTipo(String tipo);
}
