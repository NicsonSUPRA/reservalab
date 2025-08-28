package com.uespi.reservalab.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.services.ReservaService;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // Criar nova reserva
    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody Reserva reserva) {
        reservaService.salvar(reserva);
        return ResponseEntity.ok(reserva);
    }

    // Atualizar reserva
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> atualizarReserva(@PathVariable Long id, @RequestBody Reserva reserva) {
        Reserva existente = reservaService.findById(id);
        if (Utils.isEmpty(existente)) {
            return ResponseEntity.notFound().build();
        }
        reserva.setId(id);
        reservaService.atualizar(reserva);
        return ResponseEntity.ok(reserva);
    }

    // Deletar reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReserva(@PathVariable Long id) {
        Reserva existente = reservaService.findById(id);
        if (Utils.isEmpty(existente)) {
            return ResponseEntity.notFound().build();
        }
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> findReservaPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.findById(id);
        if (Utils.isEmpty(reserva)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reserva);
    }

    // Listar todas as reservas
    @GetMapping
    public ResponseEntity<List<Reserva>> listarReservas() {
        List<Reserva> reservas = reservaService.findAll();
        return ResponseEntity.ok(reservas);
    }

    // Listar reservas de um laboratório
    @GetMapping("/laboratorio/{id}")
    public ResponseEntity<List<Reserva>> listarReservasPorLaboratorio(@PathVariable Long id) {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(id);
        List<Reserva> reservas = reservaService.findByLaboratorio(laboratorio);
        return ResponseEntity.ok(reservas);
    }

    // Listar reservas de um usuário
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Reserva>> listarReservasPorUsuario(@PathVariable String id) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.fromString(id)); // Converte String para UUID
        List<Reserva> reservas = reservaService.findByUsuario(usuario);
        return ResponseEntity.ok(reservas);
    }

    // Aprovar reserva
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovarReserva(@PathVariable Long id) {
        reservaService.aprovarReserva(id);
        return ResponseEntity.ok().build();
    }

    // Recusar reserva
    @PutMapping("/{id}/recusar")
    public ResponseEntity<Void> recusarReserva(@PathVariable Long id) {
        reservaService.recusarReserva(id);
        return ResponseEntity.ok().build();
    }

    // Cancelar reserva
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.ok().build();
    }
}
