package com.uespi.reservalab.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.uespi.reservalab.dto.ReservaFixaDTO;
import com.uespi.reservalab.dto.ReservaNormalDTO;
import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.services.ReservaService;
import com.uespi.reservalab.services.UsuarioService;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    private final UsuarioService usuarioService;

    @GetMapping("/usuario/logado/info")
    public ResponseEntity<String> infoUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }

        // Nome do usuário logado
        String username = auth.getName();

        // Roles do usuário
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Usuário logado: ").append(username).append("\n");
        sb.append("Roles: ").append(roles).append("\n");

        return ResponseEntity.ok(sb.toString());
    }

    // Criar nova reserva
    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@RequestBody Reserva reserva) {
        // Pega o usuário logado do contexto Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Usuário autenticado: " + authentication.getName());
        Usuario usuarioLogado = usuarioService.obterUsuarioPorLogin(authentication.getName());

        // Passa o usuário logado para o serviço
        reservaService.salvar(reserva, usuarioLogado);

        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/fixa")
    public ResponseEntity<Reserva> criarReservaFixa(@RequestBody ReservaFixaDTO dto) {
        // Pega o usuário logado do contexto Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Usuário autenticado: " + authentication.getName());
        Usuario usuarioLogado = usuarioService.obterUsuarioPorLogin(authentication.getName());

        // Chama o serviço para salvar reserva fixa
        Reserva reserva = reservaService.salvarFixa(dto, usuarioLogado);

        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/normal")
    public ResponseEntity<Reserva> criarReservaNormal(@RequestBody ReservaNormalDTO dto) {
        // Pega o usuário logado do contexto Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Usuário autenticado: " + authentication.getName());
        Usuario usuarioLogado = usuarioService.obterUsuarioPorLogin(authentication.getName());

        // Chama o serviço para salvar reserva normal
        Reserva reserva = reservaService.salvarNormal(dto, usuarioLogado);

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

    @GetMapping("/laboratorio/{id}/periodo")
    public ResponseEntity<List<Reserva>> buscarReservasPorPeriodo(
            @PathVariable Long id,
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {

        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(id);

        LocalDateTime inicio = LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime fim = LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        List<Reserva> reservas = reservaService.buscarReservasPorPeriodo(laboratorio, inicio, fim);
        return ResponseEntity.ok(reservas);
    }

}
