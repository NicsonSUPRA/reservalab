package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.services.SemestreService;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("semestre")
@RequiredArgsConstructor
public class SemestreController {

    private final SemestreService semestreService;

    @PostMapping
    public ResponseEntity<Void> cadastrarSemestre(@RequestBody Semestre semestre) {
        semestreService.salvar(semestre);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(semestre.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semestre> findSemestrePorId(@PathVariable Long id) {
        Semestre semestre = semestreService.findById(id);
        if (semestre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(semestre);
    }

}
