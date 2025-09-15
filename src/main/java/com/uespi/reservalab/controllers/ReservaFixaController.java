// package com.uespi.reservalab.controllers;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.uespi.reservalab.dto.LiberacaoDTO;
// import com.uespi.reservalab.models.ReservaFixa;
// import com.uespi.reservalab.models.ReservaFixaLiberacao;
// import com.uespi.reservalab.services.ReservaFixaService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/reserva-fixa")
// @RequiredArgsConstructor
// public class ReservaFixaController {

// private final ReservaFixaService reservaFixaService;
// private static final DateTimeFormatter ISO_DATE =
// DateTimeFormatter.ISO_LOCAL_DATE;

// @PostMapping
// public ResponseEntity<ReservaFixa> criar(@RequestBody ReservaFixa
// reservaFixa) {
// ReservaFixa criado = reservaFixaService.criarReservaFixa(reservaFixa);
// return ResponseEntity.ok(criado);
// }

// @GetMapping
// public ResponseEntity<List<ReservaFixa>> listar() {
// return ResponseEntity.ok(reservaFixaService.findAll());
// }

// @GetMapping("/{id}")
// public ResponseEntity<ReservaFixa> buscar(@PathVariable Long id) {
// return reservaFixaService.findById(id)
// .map(ResponseEntity::ok)
// .orElse(ResponseEntity.notFound().build());
// }

// @PutMapping("/{id}")
// public ResponseEntity<ReservaFixa> atualizar(@PathVariable Long id,
// @RequestBody ReservaFixa body) {
// return reservaFixaService.findById(id)
// .map(existing -> {
// body.setId(existing.getId());
// ReservaFixa atualizado = reservaFixaService.criarReservaFixa(body);
// return ResponseEntity.ok(atualizado);
// })
// .orElse(ResponseEntity.notFound().build());
// }

// @DeleteMapping("/{id}")
// public ResponseEntity<Void> deletar(@PathVariable Long id) {
// reservaFixaService.delete(id);
// return ResponseEntity.noContent().build();
// }

// /**
// * Liberação pontual: professor (ou admin) informa que naquela data específica
// a
// * reserva fixa
// * não será usada.
// * Recebe body JSON: { "dataOcorrencia": "2025-09-22", "motivo": "...",
// * "createdBy": "uuid" }
// */
// @PostMapping("/{id}/liberar")
// public ResponseEntity<ReservaFixaLiberacao> liberar(
// @PathVariable Long id,
// @RequestBody LiberacaoDTO request) {

// LocalDate data = LocalDate.parse(request.getDataOcorrencia(), ISO_DATE);
// UUID createdBy = (request.getCreatedBy() != null &&
// !request.getCreatedBy().isBlank())
// ? UUID.fromString(request.getCreatedBy())
// : null;

// ReservaFixaLiberacao liberacao = reservaFixaService.liberarOcorrencia(id,
// data, createdBy, request.getMotivo());
// return ResponseEntity.ok(liberacao);
// }
// }
