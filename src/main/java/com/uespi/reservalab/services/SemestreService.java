package com.uespi.reservalab.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.repositories.SemestreRepository;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SemestreService {

    private final SemestreRepository semestreRepository;

    // Salvar novo semestre
    public void salvar(Semestre semestre) {
        semestreRepository.save(semestre);
    }

    // Atualizar semestre existente
    public void atualizar(Semestre semestre) {
        if (Utils.isEmpty(semestre.getId())) {
            throw new IllegalArgumentException("O ID do semestre não pode ser nulo para atualização");
        }
        Semestre semestreEncontrado = findById(semestre.getId());
        if (Utils.isNotEmpty(semestre.getDataInicio())) {
            semestreEncontrado.setDataInicio(semestre.getDataInicio());
        }
        if (Utils.isNotEmpty(semestre.getDataFim())) {
            semestreEncontrado.setDataFim(semestre.getDataFim());
        }
        if (Utils.isNotEmpty(semestre.getAno())) {
            semestreEncontrado.setAno(semestre.getAno());
        }
        if (Utils.isNotEmpty(semestre.getPeriodo())) {
            semestreEncontrado.setPeriodo(semestre.getPeriodo());
        }
        if (Utils.isNotEmpty(semestre.getDescricao())) {
            semestreEncontrado.setDescricao(semestre.getDescricao());
        }

        semestreRepository.save(semestreEncontrado);
    }

    // Deletar semestre
    public void deletar(Long id) {
        semestreRepository.deleteById(id);
    }

    // Buscar por ID
    public Semestre findById(Long id) {
        return semestreRepository.findById(id).orElse(null);
    }

    // Listar todos
    public List<Semestre> findAll() {
        return semestreRepository.findAll();
    }

    // Buscar por ano e período
    public Semestre findByAnoAndPeriodo(int ano, int periodo) {
        return semestreRepository.findByAnoAndPeriodo(ano, periodo).orElse(null);
    }
}
