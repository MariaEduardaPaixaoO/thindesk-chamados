package com.pfc.thindesk.service;

import com.pfc.thindesk.dto.IndicadoresDashboard;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.StatusChamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final int DIAS_PERIODO = 30;
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public IndicadoresDashboard gerarIndicadores() {
        IndicadoresDashboard indicadores = new IndicadoresDashboard();

        Map<StatusChamado, Long> totalPorStatus = new LinkedHashMap<>();
        Map<String, Long> porStatus = new LinkedHashMap<>();
        for (StatusChamado status : StatusChamado.values()) {
            long total = chamadoRepository.countByStatus(status);
            totalPorStatus.put(status, total);
            porStatus.put(status.getDescricao(), total);
        }

        indicadores.setTotal(chamadoRepository.count());
        indicadores.setAbertos(totalPorStatus.get(StatusChamado.ABERTO));
        indicadores.setEmAndamento(totalPorStatus.get(StatusChamado.EM_ANDAMENTO));
        indicadores.setConcluidos(totalPorStatus.get(StatusChamado.CONCLUIDO));
        indicadores.setCancelados(totalPorStatus.get(StatusChamado.CANCELADO));
        indicadores.setPorStatus(porStatus);
        indicadores.setPorTipo(agruparPor("tipo", "Sem tipo"));
        indicadores.setPorTecnico(agruparPor("tecnico", "Sem responsável"));
        preencherAberturasPorDia(indicadores);
        indicadores.setUltimosChamados(chamadoRepository.findTop5ByOrderByDataAberturaDesc());
        return indicadores;
    }

    /** Conta os chamados agrupados por um campo, do maior para o menor total. */
    private Map<String, Long> agruparPor(String campo, String rotuloVazio) {
        Aggregation agregacao = Aggregation.newAggregation(
                Aggregation.group(campo).count().as("total"),
                Aggregation.sort(Sort.Direction.DESC, "total"));

        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Document item : mongoTemplate.aggregate(agregacao, "chamados", Document.class).getMappedResults()) {
            Object chave = item.get("_id");
            String rotulo = chave == null || chave.toString().isBlank() ? rotuloVazio : chave.toString();
            resultado.merge(rotulo, ((Number) item.get("total")).longValue(), Long::sum);
        }
        return resultado;
    }

    /** Quantidade de chamados abertos em cada um dos últimos 30 dias. */
    private void preencherAberturasPorDia(IndicadoresDashboard indicadores) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusDays(DIAS_PERIODO - 1);

        Map<LocalDate, Long> totalPorDia = new TreeMap<>();
        for (LocalDate dia = inicio; !dia.isAfter(hoje); dia = dia.plusDays(1)) {
            totalPorDia.put(dia, 0L);
        }
        for (Chamado chamado : chamadoRepository.findByDataAberturaGreaterThanEqual(inicio.atStartOfDay())) {
            if (chamado.getDataAbertura() != null) {
                totalPorDia.computeIfPresent(chamado.getDataAbertura().toLocalDate(), (dia, total) -> total + 1);
            }
        }

        List<String> rotulos = new ArrayList<>();
        List<Long> totais = new ArrayList<>();
        totalPorDia.forEach((dia, total) -> {
            rotulos.add(dia.format(FORMATO_DIA));
            totais.add(total);
        });
        indicadores.setDiasRotulos(rotulos);
        indicadores.setDiasTotais(totais);
    }
}
