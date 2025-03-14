package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.uespi.reservalab.dto.UsuarioDTO;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/admin")
    public void cadastrarUsuarioAdmin(@RequestBody UsuarioDTO usuarioDTO) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setAuthorities(Arrays.asList("ADMIN"));
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuarioService.salvar(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/professorComputacao")
    public void cadastrarUsuarioProfessorComputacao(@RequestBody UsuarioDTO usuarioDTO) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setAuthorities(Arrays.asList("PROF_COMP"));
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuarioService.salvar(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/professor")
    public void cadastrarUsuarioProfessor(@RequestBody UsuarioDTO usuarioDTO) throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setAuthorities(Arrays.asList("PROF"));
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuarioService.salvar(usuario);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        usuarioService.atualizar(usuario);
    }

    @GetMapping()
    public ResponseEntity<List<Usuario>> findAllUsuarios(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("{id}")
    public Usuario buscarPorId(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        return usuarioService.obterUsuarioPorId(uuid);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Usuario>> getMethodName(@PathVariable("nome") String nome) {
        return ResponseEntity.ok().body(usuarioService.obterUsuarioComNomeSemelhante(nome));
    }

}
