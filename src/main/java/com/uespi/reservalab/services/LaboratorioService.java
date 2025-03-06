package com.uespi.reservalab.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.repositories.LaboratorioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    public void salvar(Laboratorio laboratorio) {
        laboratorioRepository.save(laboratorio);
    }

    public List<Laboratorio> findAll() {
        return laboratorioRepository.findAll();
    }
}
