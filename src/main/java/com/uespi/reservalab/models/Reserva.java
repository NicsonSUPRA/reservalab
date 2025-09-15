package com.uespi.reservalab.models;

import com.uespi.reservalab.enums.StatusReserva;
import com.uespi.reservalab.enums.TipoReserva;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
public class Reserva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    private StatusReserva status;

    // 🔹 Tipo da reserva: NORMAL ou FIXA
    @Enumerated(EnumType.STRING)
    private TipoReserva tipo;

    // 🔹 Só será usado quando tipo = FIXA
    private Integer diaSemana; // 1 = segunda ... 7 = domingo
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;
}
