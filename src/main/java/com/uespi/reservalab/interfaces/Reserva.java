package com.uespi.reservalab.interfaces;

import java.time.LocalDateTime;

import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera automaticamente getters, setters, equals, hashCode e toString
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os argumentos
public class Reserva {
    private Semestre semestre;
    private Usuario usuario;
    private LocalDateTime diaReserva;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
}
