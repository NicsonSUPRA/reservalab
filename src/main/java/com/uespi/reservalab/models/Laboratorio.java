package com.uespi.reservalab.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Laboratorio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    @JsonIgnore
    @CreatedDate
    private LocalDateTime dataCadastro;

    @JsonIgnore
    @LastModifiedDate
    private LocalDateTime dataAtualizacao;

    // @LastModifiedBy
    // private Usuario usuario;
}
