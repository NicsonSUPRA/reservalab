package com.uespi.reservalab.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uespi.reservalab.models.Laboratorio;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, UUID> {

}
