// package com.uespi.reservalab.services;

// import java.time.*;
// import java.time.temporal.TemporalAdjusters;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.uespi.reservalab.enums.StatusReserva;
// import com.uespi.reservalab.models.*;
// import com.uespi.reservalab.repositories.*;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ReservaFixaService {

// private final ReservaFixaRepository reservaFixaRepository;
// private final ReservaFixaLiberacaoRepository reservaFixaLiberacaoRepository;
// private final ReservaRepository reservaRepository;
// private final UsuarioRepository usuarioRepository;
// private final LaboratorioRepository laboratorioRepository;
// private final SemestreRepository semestreRepository;

// /**
// * Cria uma reserva fixa (validação básica).
// */
// @Transactional
// public ReservaFixa criarReservaFixa(ReservaFixa fixa) {
// // validações básicas
// if (fixa.getHoraInicio().isAfter(fixa.getHoraFim()) ||
// fixa.getHoraInicio().equals(fixa.getHoraFim())) {
// throw new IllegalArgumentException("horaInicio deve ser anterior a horaFim");
// }
// // checar semestres / usuário / laboratorio existirem
// usuarioRepository.findById(fixa.getUsuario().getId())
// .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
// laboratorioRepository.findById(fixa.getLaboratorio().getId())
// .orElseThrow(() -> new IllegalArgumentException("Laboratório não
// encontrado"));
// semestreRepository.findById(fixa.getSemestre().getId())
// .orElseThrow(() -> new IllegalArgumentException("Semestre não encontrado"));

// // poderia checar conflito com outras ReservaFixa aqui...

// return reservaFixaRepository.save(fixa);
// }

// public Optional<ReservaFixa> findById(Long id) {
// return reservaFixaRepository.findById(id);
// }

// public List<ReservaFixa> findByLaboratorio(Laboratorio lab) {
// return reservaFixaRepository.findByLaboratorioAndAtivoTrue(lab);
// }

// public List<ReservaFixa> findAll() {
// return reservaFixaRepository.findAll();
// }

// @Transactional
// public void delete(Long id) {
// reservaFixaRepository.deleteById(id);
// }

// /**
// * Registra uma liberação pontual (prof avisou que não usará)
// */
// @Transactional
// public ReservaFixaLiberacao liberarOcorrencia(Long reservaFixaId, LocalDate
// dataOcorrencia,
// java.util.UUID createdBy, String motivo) {
// ReservaFixa fixa = reservaFixaRepository.findById(reservaFixaId)
// .orElseThrow(() -> new IllegalArgumentException("Reserva fixa não
// encontrada"));

// // opcional: verificar se dataOcorrencia cai num dia compatível com diaSemana
// ReservaFixaLiberacao liberacao = new ReservaFixaLiberacao();
// liberacao.setReservaFixa(fixa);
// liberacao.setDataOcorrencia(dataOcorrencia);
// liberacao.setCreatedBy(createdBy);
// liberacao.setMotivo(motivo);
// return reservaFixaLiberacaoRepository.save(liberacao);
// }

// /**
// * Gera ocorrências virtuais (instâncias de Reserva) para reservas fixas
// * dentro do intervalo [inicio, fim] para um laboratório específico.
// *
// * Não persiste essas ocorrências — são objetos gerados para
// retorno/filtering.
// */
// public List<Reserva> gerarOcorrenciasFixasNoPeriodo(Laboratorio laboratorio,
// LocalDateTime inicio,
// LocalDateTime fim) {
// List<Reserva> resultados = new ArrayList<>();

// List<ReservaFixa> fixas =
// reservaFixaRepository.findByLaboratorioAndAtivoTrue(laboratorio);

// for (ReservaFixa fixa : fixas) {
// // determinar intervalo válido por data (usar semestre ou
// // dataInicioValida/dataFimValida)
// LocalDate periodoInicio = (fixa.getDataInicioValida() != null) ?
// fixa.getDataInicioValida()
// : fixa.getSemestre().getDataInicio().toLocalDate();
// LocalDate periodoFim = (fixa.getDataFimValida() != null) ?
// fixa.getDataFimValida()
// : fixa.getSemestre().getDataFim().toLocalDate();

// LocalDate searchStart = (inicio.toLocalDate().isAfter(periodoInicio)) ?
// inicio.toLocalDate()
// : periodoInicio;
// LocalDate searchEnd = (fim.toLocalDate().isBefore(periodoFim)) ?
// fim.toLocalDate() : periodoFim;

// if (searchStart.isAfter(searchEnd)) {
// continue; // sem ocorrências no período
// }

// DayOfWeek targetDay = DayOfWeek.of(fixa.getDiaSemana()); // 1..7 -> DayOfWeek
// // ajusta para o primeiro dia >= searchStart que tem o dia da semana desejado
// LocalDate cur = searchStart.with(TemporalAdjusters.nextOrSame(targetDay));

// while (!cur.isAfter(searchEnd)) {
// LocalDateTime occStart = LocalDateTime.of(cur, fixa.getHoraInicio());
// LocalDateTime occEnd = LocalDateTime.of(cur, fixa.getHoraFim());

// // verificar sobreposição com [inicio, fim]
// boolean intersects = !occEnd.isBefore(inicio) && !occStart.isAfter(fim);
// if (intersects) {
// // verificar se existe liberação pontual para esta data
// boolean liberada = reservaFixaLiberacaoRepository
// .existsByReservaFixaIdAndDataOcorrencia(fixa.getId(), cur);

// // verificar se já existe reserva concreta conflitante (persistida)
// boolean existeReservaConcreta = reservaRepository
// .existsByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(laboratorio,
// occEnd,
// occStart);

// if (!liberada && !existeReservaConcreta) {
// Reserva r = new Reserva();
// r.setId(null); // virtual
// r.setDataInicio(occStart);
// r.setDataFim(occEnd);
// r.setStatus(StatusReserva.FIXA); // precisa ter esse valor no enum (ou usar
// PENDENTE e marcar
// // isFixa)
// r.setUsuario(fixa.getUsuario());
// r.setLaboratorio(laboratorio);
// r.setSemestre(fixa.getSemestre());
// resultados.add(r);
// }
// }
// cur = cur.plusWeeks(1);
// }
// }

// return resultados;
// }
// }
