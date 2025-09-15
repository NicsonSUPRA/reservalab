package com.uespi.reservalab.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.enums.StatusReserva;
import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.ReservaRepository;
import com.uespi.reservalab.repositories.SemestreRepository;
import com.uespi.reservalab.repositories.UsuarioRepository;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SemestreRepository semestreRepository;
    private final UsuarioRepository usuarioRepository;
    // private final ReservaFixaRepository reservaFixaRepository;

    // Salvar nova reserva
    public void salvar(Reserva reserva) {
        // Verifica se a reserva veio com laboratório
        if (reserva.getLaboratorio() == null) {
            throw new IllegalArgumentException("Laboratório não definido para a reserva");
        }

        // Define semestre ativo com base na data atual
        LocalDateTime agora = LocalDateTime.now();
        Semestre semestreAtivo = semestreRepository.findAll()
                .stream()
                .filter(s -> !agora.isBefore(s.getDataInicio()) && !agora.isAfter(s.getDataFim()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum semestre ativo encontrado"));

        reserva.setSemestre(semestreAtivo);

        // Carrega usuário do banco
        UUID usuarioId = reserva.getUsuario().getId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        reserva.setUsuario(usuario);

        // Determina se é reserva normal ou fixa
        boolean isFixa = reserva.getStatus() == StatusReserva.FIXA;

        // Verifica conflito apenas para reservas normais
        if (!isFixa) {
            List<Reserva> reservasExistentes = reservaRepository
                    .findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(
                            reserva.getLaboratorio(),
                            reserva.getDataFim(),
                            reserva.getDataInicio());

            reservasExistentes = reservasExistentes.stream()
                    .filter(r -> r.getStatus() != StatusReserva.FIXA) // Ignora reservas fixas
                    .collect(Collectors.toList());

            if (!reservasExistentes.isEmpty()) {
                throw new IllegalArgumentException("Horário do laboratório já reservado nesse período");
            }

            // Status inicial para reservas normais
            reserva.setStatus(StatusReserva.PENDENTE);
        } else {
            // Para reservas fixas, mantém o status FIXA
            reserva.setStatus(StatusReserva.FIXA);
        }

        // Log para debug
        System.out.println("Salvando reserva " + (isFixa ? "FIXA" : "NORMAL") +
                " para usuário: " + usuario.getLogin() +
                ", laboratório: " + reserva.getLaboratorio().getNome() +
                ", início: " + reserva.getDataInicio() +
                ", fim: " + reserva.getDataFim());

        // Salva no banco
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

    public List<Reserva> buscarReservasPorPeriodo(Laboratorio laboratorio, LocalDateTime dataInicio,
            LocalDateTime dataFim) {

        List<Reserva> resultado = new ArrayList<>();

        // 1️⃣ Buscar reservas normais
        List<Reserva> reservasNormais = reservaRepository
                .findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(laboratorio, dataFim, dataInicio)
                .stream()
                .filter(r -> r.getStatus() != StatusReserva.FIXA) // ignora FIXAS aqui
                .collect(Collectors.toList());
        System.out.println("🔍 Reservas normais encontradas: " + reservasNormais.size());
        resultado.addAll(reservasNormais);

        // 2️⃣ Buscar reservas FIXAS
        List<Reserva> reservasFixas = reservaRepository.findByLaboratorioAndStatus(laboratorio, StatusReserva.FIXA);
        System.out.println("🔍 Reservas FIXAS encontradas no banco: " + reservasFixas.size());

        for (Reserva fixa : reservasFixas) {

            if (fixa.getDiaSemana() == null || fixa.getHoraInicio() == null || fixa.getHoraFim() == null) {
                System.out.println("⛔ Ignorada reserva fixa com informações incompletas -> id=" + fixa.getId());
                continue;
            }

            // Define período de verificação limitado ao semestre
            LocalDate semestreInicio = fixa.getSemestre() != null ? fixa.getSemestre().getDataInicio().toLocalDate()
                    : dataInicio.toLocalDate();
            LocalDate semestreFim = fixa.getSemestre() != null ? fixa.getSemestre().getDataFim().toLocalDate()
                    : dataFim.toLocalDate();

            LocalDate start = dataInicio.toLocalDate().isAfter(semestreInicio) ? dataInicio.toLocalDate()
                    : semestreInicio;
            LocalDate end = dataFim.toLocalDate().isBefore(semestreFim) ? dataFim.toLocalDate() : semestreFim;

            System.out.println("📅 Verificando reservas FIXAS no intervalo: " + start + " até " + end);

            LocalDate current = start;
            while (!current.isAfter(end)) {
                int diaAtual = current.getDayOfWeek().getValue(); // 1=segunda ... 7=domingo
                System.out.println("➡️ Data atual: " + current + " (diaAtual=" + diaAtual + ")");

                if (diaAtual == fixa.getDiaSemana()) {
                    System.out.println("✅ Adicionando reserva FIXA em " + current +
                            " (" + fixa.getHoraInicio() + " - " + fixa.getHoraFim() + ")");

                    Reserva r = new Reserva();
                    r.setId(fixa.getId()); // reserva gerada dinamicamente
                    r.setUsuario(fixa.getUsuario());
                    r.setLaboratorio(fixa.getLaboratorio());
                    r.setDataInicio(LocalDateTime.of(current, fixa.getHoraInicio()));
                    r.setDataFim(LocalDateTime.of(current, fixa.getHoraFim()));
                    r.setStatus(StatusReserva.FIXA); // deixa claro que é FIXA
                    r.setSemestre(fixa.getSemestre());

                    resultado.add(r);
                }

                current = current.plusDays(1);
            }
        }

        resultado.sort(Comparator.comparing(Reserva::getDataInicio));
        System.out.println("📊 Total de reservas retornadas no período (normais + fixas): " + resultado.size());

        return resultado;
    }

}
