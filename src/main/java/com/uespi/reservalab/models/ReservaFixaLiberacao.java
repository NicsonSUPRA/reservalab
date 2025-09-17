// package com.uespi.reservalab.models;

// import java.io.Serializable;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.UUID;

// import javax.persistence.*;

// import lombok.Data;

// @Entity
// @Data
// public class ReservaFixaLiberacao implements Serializable {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @ManyToOne(optional = false)
// @JoinColumn(name = "reserva_fixa_id", nullable = false)
// private ReservaFixa reservaFixa;

// @Column(name = "data_ocorrencia", nullable = false)
// private LocalDate dataOcorrencia;

// @Column(name = "motivo")
// private String motivo;

// @Column(name = "created_by")
// private UUID createdBy;

// @Column(name = "created_at")
// private LocalDateTime createdAt = LocalDateTime.now();
// }
