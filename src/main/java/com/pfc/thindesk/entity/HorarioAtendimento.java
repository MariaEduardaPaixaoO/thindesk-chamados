package com.pfc.thindesk.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "horariosAtendimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtendimento {

    @Id
    private String id;

    @NotBlank(message = "Informe o setor")
    @Size(max = 60, message = "O setor deve ter no máximo 60 caracteres")
    private String setor;

    @NotBlank(message = "Selecione o dia da semana")
    private String diaSemana; // Exemplo: "Segunda-feira"

    @NotBlank(message = "Informe o horário inicial")
    private String horarioInicio; // Exemplo: "08:00"

    @NotBlank(message = "Informe o horário final")
    private String horarioFim; // Exemplo: "18:00"
}
