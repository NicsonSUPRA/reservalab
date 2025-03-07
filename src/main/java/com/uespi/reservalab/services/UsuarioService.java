package com.uespi.reservalab.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public void salvar(Usuario usuario) throws Exception {

        if (obterUsuarioPorLogin(usuario.getLogin()) != null) {
            throw new Exception("login inválido");
        }

        usuarioRepository.save(usuario);
    }

    public void atualizar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario obterUsuarioPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario obterUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id).orElse(null);
    }

}
