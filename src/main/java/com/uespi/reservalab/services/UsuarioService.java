package com.uespi.reservalab.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.uespi.reservalab.config.UsuarioValodator;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final UsuarioValodator usuarioValodator;

    public void salvar(Usuario usuario) throws Exception {

        usuarioValodator.validar(usuario);

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

    public List<Usuario> obterUsuarioComNomeSemelhante(String nome) {
        return usuarioRepository.obterUsuarioComNomeSemelhante("%" + nome + "%");
    }

}
