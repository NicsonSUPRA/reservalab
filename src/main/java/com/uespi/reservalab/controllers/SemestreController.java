package com.uespi.reservalab.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.services.SemestreService;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/semestre")
@RequiredArgsConstructor
public class SemestreController {

    private final SemestreService semestreService;

    // Cadastrar semestre
    @PostMapping
    public ResponseEntity<Void> cadastrarSemestre(@RequestBody Semestre semestre) {
        semestreService.salvar(semestre);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(semestre.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    // Atualizar semestre
    @PutMapping("/{id}")
    public ResponseEntity<Semestre> atualizarSemestre(@PathVariable Long id, @RequestBody Semestre semestre) {
        if (Utils.isEmpty(semestreService.findById(id))) {
            System.out.println("Semestre não encontrado");
            return ResponseEntity.notFound().build();
        }
        semestre.setId(id);
        semestreService.atualizar(semestre);
        return ResponseEntity.ok(semestre);
    }

    // Deletar semestre
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSemestre(@PathVariable Long id) {
        if (Utils.isEmpty(semestreService.findById(id))) {
            return ResponseEntity.notFound().build();
        }
        semestreService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar semestre por ID
    @GetMapping("/{id}")
    public ResponseEntity<Semestre> findSemestrePorId(@PathVariable Long id) {
        Semestre semestre = semestreService.findById(id);
        if (Utils.isEmpty(semestre)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(semestre);
    }

    // Buscar todos os semestres
    @GetMapping
    public ResponseEntity<List<Semestre>> listarSemestres() {
        List<Semestre> semestres = semestreService.findAll();
        return ResponseEntity.ok(semestres);
    }

    // Buscar semestre por ano e período
    @GetMapping("/buscar")
    public ResponseEntity<Semestre> buscarPorAnoPeriodo(@RequestParam int ano, @RequestParam int periodo) {
        Semestre semestre = semestreService.findByAnoAndPeriodo(ano, periodo);
        if (semestre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(semestre);
    }
}
