// package com.uespi.reservalab.models;

// import java.io.Serializable;
// import java.time.LocalDate;
// import java.time.LocalTime;

// import javax.persistence.*;

// import lombok.Data;

// @Entity
// @Data
// public class ReservaFixa implements Serializable {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @ManyToOne(optional = false)
// @JoinColumn(name = "usuario_id", nullable = false)
// private Usuario usuario; // PROFESSOR (de preferência PROF_COMP)

// @ManyToOne(optional = false)
// @JoinColumn(name = "laboratorio_id", nullable = false)
// private Laboratorio laboratorio;

// @ManyToOne(optional = false)
// @JoinColumn(name = "semestre_id", nullable = false)
// private Semestre semestre;

// // armazenar como número no banco (1..7) mas em Java vamos usar DayOfWeek ao
// // manipular
// @Column(name = "dia_semana", nullable = false)
// private int diaSemana; // 1 = MON ... 7 = SUN

// @Column(name = "hora_inicio", nullable = false)
// private LocalTime horaInicio;

// @Column(name = "hora_fim", nullable = false)
// private LocalTime horaFim;

// @Column(name = "data_inicio_valida")
// private LocalDate dataInicioValida;

// @Column(name = "data_fim_valida")
// private LocalDate dataFimValida;

// @Column(name = "ativo", nullable = false)
// private boolean ativo = true;

// // timestamps automáticos podem ser adicionados por auditing se quiser
// }
