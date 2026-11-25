package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.exception.RegraNegocioException;
import com.pfc.thindesk.repository.HorarioAtendimentoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HorarioAtendimentoService {

    @Autowired
    private HorarioAtendimentoRepository horarioAtendimentoRepository;

    public List<HorarioAtendimento> listarTodos() {
        return horarioAtendimentoRepository.findAll();
    }

    public List<HorarioAtendimento> listarPorSetor(String setor) {
        return horarioAtendimentoRepository.findBySetor(setor);
    }

    public HorarioAtendimento salvar(HorarioAtendimento horarioAtendimento) {
        // Horários no formato HH:mm podem ser comparados como texto
        if (horarioAtendimento.getHorarioInicio().compareTo(horarioAtendimento.getHorarioFim()) >= 0) {
            throw new RegraNegocioException("horarioFim", "O horário final deve ser posterior ao horário inicial");
        }
        horarioAtendimento.setSetor(horarioAtendimento.getSetor().trim());
        return horarioAtendimentoRepository.save(horarioAtendimento);
    }

    public void deletar(String id) {
        horarioAtendimentoRepository.deleteById(id);
    }
}
