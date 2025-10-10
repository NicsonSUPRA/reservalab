package com.uespi.reservalab.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uespi.reservalab.dto.ReservaFixaDTO;
import com.uespi.reservalab.dto.ReservaFixaExcecaoDTO;
import com.uespi.reservalab.dto.ReservaNormalDTO;
import com.uespi.reservalab.enums.StatusReserva;
import com.uespi.reservalab.enums.TipoReserva;
import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.models.Reserva;
import com.uespi.reservalab.models.ReservaFixaExcecao;
import com.uespi.reservalab.models.Semestre;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.LaboratorioRepository;
import com.uespi.reservalab.repositories.ReservaFixaExcecaoRepository;
import com.uespi.reservalab.repositories.ReservaRepository;
import com.uespi.reservalab.repositories.SemestreRepository;
import com.uespi.reservalab.repositories.UsuarioRepository;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final UsuarioService usuarioService;
    private final LaboratorioService laboratorioService;
    private final SemestreService semestreService;
    private final ReservaFixaExcecaoRepository excecaoRepo;

    private final ReservaRepository reservaRepository;
    private final SemestreRepository semestreRepository;
    private final UsuarioRepository usuarioRepository;
    private final LaboratorioRepository laboratorioRepository;
    // private final ReservaFixaRepository reservaFixaRepository;

    // Salvar nova reserva
    @Transactional
    public Reserva salvar(Reserva reserva, Usuario usuarioLogado) {
        // 1️⃣ Validar laboratório
        if (reserva.getLaboratorio() == null || Utils.isEmpty(reserva.getLaboratorio().getId())) {
            throw new IllegalArgumentException("Laboratório não definido para a reserva");
        }
        Laboratorio laboratorio = laboratorioRepository.findById(reserva.getLaboratorio().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Laboratório não encontrado: " + reserva.getLaboratorio().getId()));
        reserva.setLaboratorio(laboratorio);

        // 2️⃣ Buscar usuário da reserva no banco
        if (reserva.getUsuario() == null || reserva.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário não definido para a reserva");
        }
        UUID usuarioId = reserva.getUsuario().getId();
        Usuario usuarioReserva = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        reserva.setUsuario(usuarioReserva);

        // 3️⃣ Definir semestre ativo
        LocalDateTime agora = LocalDateTime.now();
        Semestre semestreAtivo = semestreRepository.findAll()
                .stream()
                .filter(s -> !agora.isBefore(s.getDataInicio()) && !agora.isAfter(s.getDataFim()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum semestre ativo encontrado"));
        reserva.setSemestre(semestreAtivo);

        // 4️⃣ Tratar reserva fixa
        if (reserva.getTipo() == TipoReserva.FIXA) {
            if (!usuarioLogado.getRoles().contains("ADMIN")) {
                throw new IllegalArgumentException("Apenas administradores podem criar reservas fixas");
            }
            if (!usuarioReserva.getRoles().contains("PROF_COMP")) {
                throw new IllegalArgumentException(
                        "Reservas fixas só podem ser associadas a professores de computação");
            }
            reserva.setTipo(TipoReserva.FIXA);

        } else {
            // 5️⃣ Verificar conflito para reservas normais
            List<Reserva> reservasExistentes = reservaRepository
                    .findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(
                            laboratorio,
                            reserva.getDataFim(),
                            reserva.getDataInicio())
                    .stream()
                    .filter(r -> r.getTipo() != TipoReserva.FIXA) // Ignorar fixas
                    .filter(Reserva::isAtivo) // Apenas ativas
                    .filter(r -> r.getStatus() == null || !r.getStatus().equals(StatusReserva.CANCELADA)) // Ignorar
                                                                                                          // canceladas
                    .collect(Collectors.toList());

            if (!reservasExistentes.isEmpty()) {
                throw new IllegalArgumentException("Horário do laboratório já reservado nesse período");
            }

            reserva.setStatus(StatusReserva.PENDENTE);
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva salvarFixa(ReservaFixaDTO dto, Usuario usuarioLogado) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO de reserva fixa não pode ser nulo");
        }

        System.out.println("🔹 DTO recebido: " + dto);

        Reserva reserva = new Reserva();
        reserva.setTipo(TipoReserva.FIXA);
        reserva.setDiaSemana(dto.getDiaSemana());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFim(dto.getHoraFim());

        System.out.println("🔹 Mapeando usuário...");
        Usuario usuario = usuarioService.obterUsuarioPorId(dto.getUsuarioId());
        System.out.println("Usuario encontrado: " + usuario);
        reserva.setUsuario(usuario);

        System.out.println("🔹 Mapeando laboratório...");
        Laboratorio laboratorio = laboratorioService.obterLaboratorioPorId(dto.getLaboratorioId());
        System.out.println("Laboratório encontrado: " + laboratorio);
        reserva.setLaboratorio(laboratorio);

        System.out.println("🔹 Mapeando semestre...");
        Semestre semestre = semestreService.findById(dto.getSemestreId());
        System.out.println("Semestre encontrado: " + semestre);
        reserva.setSemestre(semestre);

        System.out.println("🔹 Chamando método salvar()...");
        Reserva salva = salvar(reserva, usuarioLogado);
        System.out.println("🔹 Reserva salva: " + salva);

        return salva;
    }

    @Transactional
    public Reserva salvarNormal(ReservaNormalDTO dto, Usuario usuarioLogado) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO de reserva normal não pode ser nulo");
        }

        System.out.println("🔹 DTO recebido (Normal): " + dto);

        Reserva reserva = new Reserva();
        reserva.setTipo(TipoReserva.NORMAL);
        reserva.setDataInicio(dto.getDataInicio());
        reserva.setDataFim(dto.getDataFim());

        System.out.println("🔹 Mapeando usuário...");
        Usuario usuario = usuarioService.obterUsuarioPorId(dto.getUsuarioId());
        System.out.println("Usuario encontrado: " + usuario);
        reserva.setUsuario(usuario);

        System.out.println("🔹 Mapeando laboratório...");
        Laboratorio laboratorio = laboratorioService.obterLaboratorioPorId(dto.getLaboratorioId());
        System.out.println("Laboratório encontrado: " + laboratorio);
        reserva.setLaboratorio(laboratorio);

        System.out.println("🔹 Mapeando semestre...");
        Semestre semestre = semestreService.findById(dto.getSemestreId());
        System.out.println("Semestre encontrado: " + semestre);
        reserva.setSemestre(semestre);

        System.out.println("🔹 Chamando método salvar()...");
        Reserva salva = salvar(reserva, usuarioLogado);
        System.out.println("🔹 Reserva salva: " + salva);

        return salva;
    }

    public void validarConflitoReserva(Reserva reserva) {
        List<Reserva> reservasExistentes = reservaRepository
                .findByLaboratorioAndDiaSemanaAndSemestre(
                        reserva.getLaboratorio(),
                        reserva.getDiaSemana(),
                        reserva.getSemestre());

        for (Reserva existente : reservasExistentes) {
            boolean sobrepoe = reserva.getHoraInicio().isBefore(existente.getHoraFim()) &&
                    reserva.getHoraFim().isAfter(existente.getHoraInicio());

            if (sobrepoe) {
                throw new IllegalArgumentException(
                        "Já existe uma reserva conflitante neste horário para este laboratório.");
            }
        }
    }

    // // Método para pegar usuário logado do contexto Spring
    // public Usuario getUsuarioLogado() {
    // org.springframework.security.core.userdetails.User user =
    // (org.springframework.security.core.userdetails.User) SecurityContextHolder
    // .getContext().getAuthentication().getPrincipal();

    // return usuarioRepository.findByLogin(user.getUsername())
    // .orElseThrow(() -> new IllegalArgumentException("Usuário logado não
    // encontrado"));
    // }

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

        // 1️⃣ Buscar reservas normais (apenas ativas e não canceladas)
        List<Reserva> reservasNormais = reservaRepository
                .findByLaboratorioAndDataInicioLessThanAndDataFimGreaterThan(laboratorio, dataFim, dataInicio)
                .stream()
                .filter(r -> r.getTipo() == TipoReserva.NORMAL) // apenas normais
                .filter(Reserva::isAtivo) // apenas ativas
                .filter(r -> r.getStatus() == null || !r.getStatus().equals(StatusReserva.CANCELADA)) // ignora
                                                                                                      // canceladas
                .collect(Collectors.toList());

        System.out.println("🔍 Reservas normais encontradas: " + reservasNormais.size());
        resultado.addAll(reservasNormais);

        // 2️⃣ Buscar reservas FIXAS (apenas ativas)
        List<Reserva> reservasFixas = reservaRepository.findByLaboratorioAndTipo(laboratorio, TipoReserva.FIXA)
                .stream()
                .filter(Reserva::isAtivo) // considerar somente fixas ativas
                .collect(Collectors.toList());
        System.out.println("🔍 Reservas FIXAS encontradas no banco: " + reservasFixas.size());

        if (!reservasFixas.isEmpty()) {
            // Define período de verificação (usado para buscar exceções)
            LocalDate periodStart = dataInicio.toLocalDate();
            LocalDate periodEnd = dataFim.toLocalDate();

            // Buscar exceções em lote para as fixas no intervalo
            List<ReservaFixaExcecao> excecoes = excecaoRepo.findByReservaFixaInAndDataBetween(reservasFixas,
                    periodStart, periodEnd);

            // Montar mapa: fixaId -> Set<LocalDate> (datas com exceção)
            Map<Long, Set<LocalDate>> excecoesMap = new HashMap<>();
            for (ReservaFixaExcecao ex : excecoes) {
                Long idFixa = ex.getReservaFixa().getId();
                excecoesMap.computeIfAbsent(idFixa, k -> new HashSet<>()).add(ex.getData());
            }

            // Iterar cada reserva fixa e gerar ocorrências, pulando as que têm exceção
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

                LocalDate current = start;
                while (!current.isAfter(end)) {
                    int diaAtual = current.getDayOfWeek().getValue(); // 1=segunda ... 7=domingo

                    if (diaAtual == fixa.getDiaSemana()) {
                        // Verifica se existe exceção para esta fixa nesta data
                        Set<LocalDate> datasExcluidas = excecoesMap.getOrDefault(fixa.getId(), Collections.emptySet());
                        if (datasExcluidas.contains(current)) {
                            // ocorrência cancelada/exceção -> pular
                            System.out.println("⛔ Ocorrência fixa pulada por exceção -> fixaId=" + fixa.getId()
                                    + " date=" + current);
                        } else {
                            Reserva r = new Reserva();
                            r.setId(fixa.getId()); // id informativo da fixa
                            r.setUsuario(fixa.getUsuario());
                            r.setLaboratorio(fixa.getLaboratorio());
                            r.setDataInicio(LocalDateTime.of(current, fixa.getHoraInicio()));
                            r.setDataFim(LocalDateTime.of(current, fixa.getHoraFim()));
                            r.setTipo(TipoReserva.FIXA);
                            r.setSemestre(fixa.getSemestre());
                            r.setAtivo(true);
                            resultado.add(r);
                        }
                    }
                    current = current.plusDays(1);
                }
            }
        }

        resultado.sort(Comparator.comparing(Reserva::getDataInicio));
        System.out.println("📊 Total de reservas retornadas no período (normais + fixas): " + resultado.size());

        return resultado;
    }

    public List<Reserva> buscarReservasFixasPorPeriodo(Laboratorio laboratorio, LocalDateTime dataInicio,
            LocalDateTime dataFim) {

        List<Reserva> resultado = new ArrayList<>();

        // Buscar reservas FIXAS do laboratório (apenas ativas)
        List<Reserva> reservasFixas = reservaRepository.findByLaboratorioAndTipo(laboratorio, TipoReserva.FIXA)
                .stream()
                .filter(Reserva::isAtivo)
                .collect(Collectors.toList());

        System.out.println("🔍 Reservas FIXAS encontradas no banco: " + reservasFixas.size());

        if (reservasFixas.isEmpty()) {
            return resultado;
        }

        // Período para buscar exceções (usamos LocalDate)
        LocalDate periodStart = dataInicio.toLocalDate();
        LocalDate periodEnd = dataFim.toLocalDate();

        // Converter lista de fixas para lista de IDs (evita problemas de tipo e é mais
        // eficiente)
        List<Long> fixasIds = reservasFixas.stream()
                .map(Reserva::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Buscar exceções em lote (se houver IDs)
        List<ReservaFixaExcecao> excecoes = fixasIds.isEmpty()
                ? Collections.emptyList()
                : excecaoRepo.findByReservaFixaIdInAndDataBetween(fixasIds, periodStart, periodEnd);

        // Montar mapa: fixaId -> Set<LocalDate> com datas que possuem exceção
        Map<Long, Set<LocalDate>> excecoesMap = new HashMap<>();
        for (ReservaFixaExcecao ex : excecoes) {
            Long idFixa = ex.getReservaFixa().getId();
            excecoesMap.computeIfAbsent(idFixa, k -> new HashSet<>()).add(ex.getData());
        }

        // Iterar cada reserva fixa e gerar ocorrências (pulando datas com exceção)
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

            LocalDate current = start;
            while (!current.isAfter(end)) {
                int diaAtual = current.getDayOfWeek().getValue(); // 1=segunda ... 7=domingo

                if (diaAtual == fixa.getDiaSemana()) {
                    // verifica se existe exceção para esta fixa nesta data
                    Set<LocalDate> datasExcluidas = excecoesMap.getOrDefault(fixa.getId(), Collections.emptySet());
                    if (datasExcluidas.contains(current)) {
                        // ocorrência cancelada/exceção -> pular
                        System.out.println(
                                "⛔ Ocorrência fixa pulada por exceção -> fixaId=" + fixa.getId() + " date=" + current);
                    } else {
                        Reserva r = new Reserva();
                        r.setId(fixa.getId()); // id informativo da fixa
                        r.setUsuario(fixa.getUsuario());
                        r.setLaboratorio(fixa.getLaboratorio());
                        r.setDataInicio(LocalDateTime.of(current, fixa.getHoraInicio()));
                        r.setDataFim(LocalDateTime.of(current, fixa.getHoraFim()));
                        r.setTipo(TipoReserva.FIXA);
                        r.setSemestre(fixa.getSemestre());
                        r.setAtivo(true);
                        resultado.add(r);
                    }
                }

                current = current.plusDays(1);
            }
        }

        resultado.sort(Comparator.comparing(Reserva::getDataInicio));
        System.out.println("📊 Total de reservas FIXAS retornadas no período: " + resultado.size());

        return resultado;
    }

    @Transactional
    public ReservaFixaExcecao cancelarOcorrenciaFixa(Long fixaId, LocalDate data, String motivo,
            Usuario usuarioLogado) {
        Reserva fixa = reservaRepository.findById(fixaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva fixa não encontrada: " + fixaId));

        // permissão: ADMIN ou dono da reserva fixa
        boolean isAdmin = usuarioLogado.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
        boolean isOwner = fixa.getUsuario() != null && usuarioLogado.getId().equals(fixa.getUsuario().getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Sem permissão para cancelar essa ocorrência");
        }

        var opt = excecaoRepo.findByReservaFixaIdAndData(fixaId, data);
        ReservaFixaExcecao ex;
        if (opt.isPresent()) {
            ex = opt.get();
            ex.setTipo("CANCELADA");
            ex.setMotivo(motivo);
            ex.setUsuarioId(usuarioLogado.getId());
            ex = excecaoRepo.save(ex);
        } else {
            ex = new ReservaFixaExcecao();
            ex.setReservaFixa(fixa);
            ex.setData(data);
            ex.setTipo("CANCELADA");
            ex.setMotivo(motivo);
            ex.setUsuarioId(usuarioLogado.getId());
            ex = excecaoRepo.save(ex);
        }
        return ex;
    }

    @Transactional
    public void removerExcecaoOcorrenciaFixa(Long fixaId, LocalDate data, Usuario usuarioLogado) {
        Reserva fixa = reservaRepository.findById(fixaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva fixa não encontrada: " + fixaId));

        boolean isAdmin = usuarioLogado.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
        boolean isOwner = fixa.getUsuario() != null && usuarioLogado.getId().equals(fixa.getUsuario().getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Sem permissão para restaurar essa ocorrência");
        }

        var opt = excecaoRepo.findByReservaFixaIdAndData(fixaId, data);
        opt.ifPresent(excecaoRepo::delete);
    }

    @Transactional
    public void criarExcecaoCancelamento(ReservaFixaExcecaoDTO dto, Usuario usuarioLogado) {
        Reserva reservaFixa = findById(dto.getReservaFixaId());
        if (reservaFixa == null || reservaFixa.getTipo() != TipoReserva.FIXA) {
            throw new IllegalArgumentException("Reserva fixa não encontrada");
        }

        ReservaFixaExcecao excecao = new ReservaFixaExcecao();
        excecao.setReservaFixa(reservaFixa);
        excecao.setData(dto.getData());
        excecao.setTipo("CANCELADA");
        excecao.setMotivo(dto.getMotivo());
        excecao.setUsuarioId(usuarioLogado.getId());

        excecaoRepo.save(excecao);
    }

}
