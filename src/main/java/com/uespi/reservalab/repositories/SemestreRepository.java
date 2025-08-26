package com.uespi.reservalab.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uespi.reservalab.models.Semestre;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {

    Optional<Semestre> findByAnoAndPeriodo(int ano, int periodo);

}
