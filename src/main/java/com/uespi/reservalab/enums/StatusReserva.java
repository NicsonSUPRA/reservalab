package com.uespi.reservalab.enums;

import java.util.Arrays;

public enum StatusReserva {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    RECUSADA("Recusada"),
    CANCELADA("Cancelada"),
    FIXA("Fixa"); // reservado por configuração fixa (ReservaFixa)

    private final String descricao;

    StatusReserva(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    // Buscar pelo nome exato do enum (case insensitive)
    public static StatusReserva fromNome(String nome) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(nome))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("StatusReserva inválido: " + nome));
    }

    // Buscar pela descrição (case insensitive)
    public static StatusReserva fromDescricao(String descricao) {
        return Arrays.stream(values())
                .filter(status -> status.getDescricao().equalsIgnoreCase(descricao))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("StatusReserva inválido: " + descricao));
    }

    // helper útil para checar se é uma reserva fixa
    public boolean isFixa() {
        return this == FIXA;
    }
}
