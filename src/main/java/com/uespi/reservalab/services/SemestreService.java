package com.uespi.reservalab.services;

import org.springframework.stereotype.Service;

import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.repositories.SemestreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemestreService {

    private final SemestreRepository semestreRepository;

    public void salvar(Semestre semestre) {
        semestreRepository.save(semestre);
    }

    public Semestre findById(Long id) {
        return semestreRepository.findById(id).orElse(null);
    }
}
