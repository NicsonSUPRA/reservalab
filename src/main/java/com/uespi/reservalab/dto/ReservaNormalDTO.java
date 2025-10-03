package com.uespi.reservalab.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.uespi.reservalab.enums.StatusReserva;

import lombok.Data;

@Data
public class ReservaNormalDTO {
    private UUID usuarioId;
    private Long laboratorioId;
    private Long semestreId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReserva status; // geralmente PENDENTE no cadastro
}
