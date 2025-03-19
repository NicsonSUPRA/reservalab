package com.uespi.reservalab.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.repositories.LaboratorioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    @Transactional
    public void salvar(Laboratorio laboratorio) {
        if (laboratorioRepository.obterLaboratorioComNomeIgual(laboratorio.getNome()).size() > 0) {
            throw new IllegalArgumentException("Já existe um laboratório com esse nome");
        }
        laboratorioRepository.save(laboratorio);
    }

    @Transactional
    public void atualizar(Laboratorio laboratorio) {
        laboratorioRepository.save(laboratorio);

        if (laboratorioRepository.obterLaboratorioComNomeIgual(laboratorio.getNome()).size() > 0) {
            throw new IllegalArgumentException("Já existe um laboratório com esse nome");
        }
    }

    @Transactional
    public void atualizarTeste(Laboratorio laboratorio) {
        // esse metodo foi feito como teste, caso o metodo tenha @Transactional, não é
        // preciso
        // dar o save, pois o objeto já está sendo gerenciado pelo JPA, fica no estado
        // de Managed
        // qualquer atualização feita no objeto é automaticamente persistida no banco de
        // dados
        // ou seja, é feito o commit automaticamente
        // se um erro ocorrer, é feito o rollback automaticamente
        Laboratorio laboratorioTeste = laboratorioRepository.findLaboratorioById(6L);
        laboratorioTeste.setNome("Laboratório de Teste");
    }

    public List<Laboratorio> findAll() {
        return laboratorioRepository.findAll();
    }

    public Laboratorio obterLaboratorioPorId(Long id) {
        return laboratorioRepository.findById(id).orElse(null);
    }

    public void deletarLaboratorioPorId(Long id) {
        laboratorioRepository.deletarLaboratorioPorId(id);
    }

}
