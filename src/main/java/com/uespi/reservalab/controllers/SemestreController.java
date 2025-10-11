package com.uespi.reservalab.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Map<String, Object>> cadastrarSemestre(@RequestBody Semestre semestre) {
        try {
            semestreService.salvar(semestre);

            // Monta a URI do semestre criado
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(semestre.getId())
                    .toUri();

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Semestre cadastrado com sucesso!");
            response.put("location", location.toString());

            return ResponseEntity.created(location).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("erro", "Erro ao cadastrar semestre: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
    @GetMapping("/pesquisar")
    public ResponseEntity<List<Semestre>> pesquisarSemestres(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer periodo) {

        List<Semestre> semestres = semestreService.pesquisarSemestres(descricao, ano, periodo);
        return ResponseEntity.ok(semestres);
    }

    @GetMapping("/por-data")
    public ResponseEntity<?> buscarSemestrePorData(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {
        try {
            Semestre semestre = semestreService.buscarPorData(data);
            if (semestre == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensagem", "Nenhum semestre encontrado para a data informada."));
            }
            return ResponseEntity.ok(semestre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao buscar semestre: " + e.getMessage()));
        }
    }

}
