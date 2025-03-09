package com.uespi.reservalab.config;

import org.springframework.stereotype.Component;

import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioValodator {

    private final UsuarioRepository usuarioRepository;

    public void validar(Usuario usuario) throws Exception {

        if (usuarioRepository.existsByLogin(usuario.getLogin())) {
            throw new IllegalArgumentException("login inválido");
        }

    }
}
