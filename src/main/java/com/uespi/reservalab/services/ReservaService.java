package com.uespi.reservalab.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.enums.StatusReserva;
import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.ReservaRepository;
import com.uespi.reservalab.repositories.SemestreRepository;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SemestreRepository semestreRepository;

    // Salvar nova reserva
    public void salvar(Reserva reserva) {
        // Define semestre ativo com base na data atual
        LocalDateTime agora = LocalDateTime.now();
        Semestre semestreAtivo = semestreRepository.findAll()
                .stream()
                .filter(s -> !agora.isBefore(s.getDataInicio()) && !agora.isAfter(s.getDataFim()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum semestre ativo encontrado"));

        reserva.setSemestre(semestreAtivo);

        // Verifica conflito de horário no mesmo laboratório
        List<Reserva> reservasExistentes = reservaRepository
                .findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(
                        reserva.getLaboratorio(),
                        reserva.getDataFim(),
                        reserva.getDataInicio());

        if (!reservasExistentes.isEmpty()) {
            throw new IllegalArgumentException("Horário do laboratório já reservado nesse período");
        }

        // Define status inicial
        reserva.setStatus(StatusReserva.PENDENTE);

        reservaRepository.save(reserva);
    }

    // Atualizar reserva existente
    public void atualizar(Reserva reserva) {
        if (Utils.isEmpty(reserva.getId())) {
            throw new IllegalArgumentException("O ID da reserva não pode ser nulo para atualização");
        }

        Reserva reservaExistente = findById(reserva.getId());

        if (Utils.isNotEmpty(reserva.getDataInicio())) {
            reservaExistente.setDataInicio(reserva.getDataInicio());
        }
        if (Utils.isNotEmpty(reserva.getDataFim())) {
            reservaExistente.setDataFim(reserva.getDataFim());
        }
        if (Utils.isNotEmpty(reserva.getLaboratorio())) {
            reservaExistente.setLaboratorio(reserva.getLaboratorio());
        }
        if (Utils.isNotEmpty(reserva.getUsuario())) {
            reservaExistente.setUsuario(reserva.getUsuario());
        }
        if (Utils.isNotEmpty(reserva.getStatus())) {
            reservaExistente.setStatus(reserva.getStatus());
        }

        // Checagem de conflito se horário/lab forem alterados
        List<Reserva> reservasExistentes = reservaRepository
                .findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(
                        reservaExistente.getLaboratorio(),
                        reservaExistente.getDataFim(),
                        reservaExistente.getDataInicio());

        boolean conflito = reservasExistentes.stream()
                .anyMatch(r -> !r.getId().equals(reservaExistente.getId()));

        if (conflito) {
            throw new IllegalArgumentException("Horário do laboratório já reservado nesse período");
        }

        reservaRepository.save(reservaExistente);
    }

    // Deletar reserva
    public void deletar(Long id) {
        reservaRepository.deleteById(id);
    }

    // Buscar por ID
    public Reserva findById(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    // Listar todas as reservas
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    // Listar reservas por laboratório
    public List<Reserva> findByLaboratorio(Laboratorio laboratorio) {
        return reservaRepository.findByLaboratorio(laboratorio);
    }

    // Listar reservas de um usuário
    public List<Reserva> findByUsuario(Usuario usuario) {
        return reservaRepository.findByUsuario(usuario);
    }

    // Aprovar uma reserva
    public void aprovarReserva(Long id) {
        Reserva reserva = findById(id);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não encontrada");
        }
        reserva.setStatus(StatusReserva.APROVADA);
        reservaRepository.save(reserva);
    }

    // Recusar uma reserva
    public void recusarReserva(Long id) {
        Reserva reserva = findById(id);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não encontrada");
        }
        reserva.setStatus(StatusReserva.RECUSADA);
        reservaRepository.save(reserva);
    }

    // Cancelar uma reserva
    public void cancelarReserva(Long id) {
        Reserva reserva = findById(id);
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não encontrada");
        }
        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
}
