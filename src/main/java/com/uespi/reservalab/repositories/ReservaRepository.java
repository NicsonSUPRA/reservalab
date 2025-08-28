package com.uespi.reservalab.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.models.Usuario;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Buscar todas as reservas de um laboratório
    List<Reserva> findByLaboratorio(Laboratorio laboratorio);

    // Buscar reservas de um usuário
    List<Reserva> findByUsuario(Usuario usuario);

    // Buscar reservas de um semestre
    List<Reserva> findBySemestre(Semestre semestre);

    // Buscar reservas de um laboratório em um período específico (para checar
    // conflito)
    List<Reserva> findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(
            Laboratorio laboratorio, LocalDateTime dataFim, LocalDateTime dataInicio);
}
