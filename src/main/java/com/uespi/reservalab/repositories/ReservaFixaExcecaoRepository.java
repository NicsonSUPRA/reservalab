package com.uespi.reservalab.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.ReservaFixaExcecao;

public interface ReservaFixaExcecaoRepository extends JpaRepository<ReservaFixaExcecao, Long> {
    List<ReservaFixaExcecao> findByReservaFixaInAndDataBetween(List<Reserva> fixas, LocalDate start, LocalDate end);

    List<ReservaFixaExcecao> findByReservaFixaIdInAndDataBetween(List<Long> reservaFixaIds, LocalDate start,
            LocalDate end);

    Optional<ReservaFixaExcecao> findByReservaFixaIdAndData(Long reservaFixaId, LocalDate data);
}
