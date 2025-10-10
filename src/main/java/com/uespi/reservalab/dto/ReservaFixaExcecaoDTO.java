package com.uespi.reservalab.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReservaFixaExcecaoDTO {

    private Long reservaFixaId; // id da reserva fixa que será cancelada
    private LocalDate data; // data específica da exceção
    private String tipo; // "CANCELADA", "BLOQUEADA", etc.
    private String motivo; // motivo da exceção
}
