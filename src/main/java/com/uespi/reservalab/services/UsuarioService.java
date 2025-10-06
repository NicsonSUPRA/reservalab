package com.uespi.reservalab.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// import com.uespi.reservalab.config.UsuarioValodator;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    // private final UsuarioValodator usuarioValodator;

    public void salvar(Usuario usuario) {

        // usuarioValodator.validar(usuario);

        usuarioRepository.save(usuario);
    }

    public void atualizar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario autenticar(String login, String senha) {
        Usuario usuario = obterUsuarioPorLogin(login);

        if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        return usuario;
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

    public List<Usuario> pesquisarUsuarios(String nome, String login, List<String> roles) {
        // Consulta usando Criteria, JPQL ou repositório JPA
        // Exemplo simples:
        return usuarioRepository.findAll().stream()
                .filter(u -> (nome == null || u.getNome().contains(nome)) &&
                        (login == null || u.getLogin().contains(login)) &&
                        (roles == null || roles.isEmpty() || u.getRoles().stream().anyMatch(roles::contains)))
                .collect(Collectors.toList());
    }

    public List<Usuario> obterTodosUsuariosProfessoresAndProfComp() {
        return usuarioRepository.findByRolesIn(List.of("PROF", "PROF_COMP"));
    }

}
