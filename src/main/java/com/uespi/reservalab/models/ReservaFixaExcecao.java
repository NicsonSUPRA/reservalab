package com.uespi.reservalab.models;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ReservaFixaExcecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_fixa_id", nullable = false)
    private Reserva reservaFixa; // aponta para a reserva tipo FIXA

    private LocalDate data; // data específica da ocorrência que foi alterada

    private String tipo; // "CANCELADA", "BLOQUEADA", etc.

    private String motivo;

    private UUID usuarioId; // opcional: quem efetuou a ação

    private LocalDateTime criadoEm = LocalDateTime.now();
}
