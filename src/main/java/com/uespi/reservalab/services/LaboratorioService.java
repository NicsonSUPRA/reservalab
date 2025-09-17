package com.uespi.reservalab.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.repositories.LaboratorioRepository;
import com.uespi.reservalab.validators.LaboratorioValidator;

import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    private final LaboratorioValidator validator;

    @Transactional
    public void salvar(Laboratorio laboratorio) {
        // if
        // (laboratorioRepository.obterLaboratorioComNomeIgual(laboratorio.getNome()).size()
        // > 0) {
        // throw new RegistroDuplicadoException("Já existe um laboratório com esse
        // nome");
        // }

        // o validator abaixo faz a mesma coisa que o codigo comentado acima, mas foi
        // implementado para seguir a regra do
        // CLEAN CODE, que é a responsabilidade única.
        validator.validarLaboratorio(laboratorio);

        laboratorioRepository.save(laboratorio);
    }

    @Transactional
    public Laboratorio atualizar(Laboratorio laboratorio) {
        validator.validarLaboratorio(laboratorio);

        Laboratorio existente = laboratorioRepository.findById(laboratorio.getId())
                .orElseThrow(() -> new EntityNotFoundException("Laboratório não encontrado"));

        // Preserva data_cadastro e outros campos que não devem ser alterados
        laboratorio.setDataCadastro(existente.getDataCadastro());

        return laboratorioRepository.save(laboratorio);
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

    public List<Laboratorio> obterLaboratoriosComNomesSemelhantes(String nome) {
        return laboratorioRepository.obterLaboratoriosComNomesSemelhantes("%" + nome + "%");
    }

}
