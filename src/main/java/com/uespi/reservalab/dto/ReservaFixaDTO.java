package com.uespi.reservalab.dto;

import java.time.LocalTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ReservaFixaDTO {
    private UUID usuarioId;
    private Long laboratorioId;
    private Long semestreId;
    private Integer diaSemana; // 1=segunda ... 7=domingo
    private LocalTime horaInicio;
    private LocalTime horaFim;
}
