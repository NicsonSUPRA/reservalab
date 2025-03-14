package com.uespi.reservalab.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.models.Laboratorio;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    public Laboratorio findLaboratorioById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE from Laboratorio l where l.id = :id")
    public void deletarLaboratorioPorId(@Param("id") Long id);

    @Query("SELECT l from Laboratorio l where l.nome = :nome")
    public List<Laboratorio> obterLaboratorioComNomeIgual(@Param("nome") String nome);

}
