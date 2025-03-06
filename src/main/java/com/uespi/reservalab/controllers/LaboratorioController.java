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
import org.springframework.web.bind.annotation.GetMapping;

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
    public ResponseEntity<List<Laboratorio>> getMethodName() {
        return ResponseEntity.ok(laboratorioService.findAll());
    }

}
