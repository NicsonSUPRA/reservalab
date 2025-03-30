package com.uespi.reservalab.validators;

import org.springframework.stereotype.Component;

import com.uespi.reservalab.exceptions.RegistroDuplicadoException;
import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.repositories.LaboratorioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LaboratorioValidator {

    private final LaboratorioRepository laboratorioRepository;

    public void validarLaboratorio(Laboratorio laboratorio) {
        if (laboratorioRepository.obterLaboratorioComNomeIgual(laboratorio.getNome()).size() > 0) {
            throw new RegistroDuplicadoException("Já existe um laboratório com esse nome");
        }
    }

}
