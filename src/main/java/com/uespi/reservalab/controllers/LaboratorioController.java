package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.services.LaboratorioService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/laboratorios")
@RequiredArgsConstructor
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarLaboratorio(@RequestBody Laboratorio laboratorio) {
        laboratorioService.salvar(laboratorio);
    }

    @GetMapping
    public ResponseEntity<List<Laboratorio>> findAll(@RequestParam(value = "nome", required = false) String nome) {
        if (nome != null) {
            return ResponseEntity.ok(laboratorioService.obterLaboratoriosComNomesSemelhantes(nome));
        }
        return ResponseEntity.ok(laboratorioService.findAll());
    }

    @PutMapping("/{id}")
    public void atualizar(@PathVariable Long id, @RequestBody Laboratorio laboratorio) {
        laboratorioService.atualizar(laboratorio);
    }

    @GetMapping("/{id}")
    public Laboratorio obterLaboratorioPorId(@PathVariable Long id) {
        return laboratorioService.obterLaboratorioPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarLaboratorio(@PathVariable Long id) {
        laboratorioService.deletarLaboratorioPorId(id);
    }

    // @GetMapping
    // public ResponseEntity<List<Laboratorio>>
    // pesquisarPorFiltro(@RequestParam(value = "nome", ) String param) {
    // return new String();
    // }

}
