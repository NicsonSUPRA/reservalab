package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.uespi.reservalab.dto.UsuarioDTO;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.security.SecurityService;
import com.uespi.reservalab.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final PasswordEncoder passwordEncoder;

    private final SecurityService securityService;

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public void cadastrarUsuario(@RequestBody Usuario usuario) throws Exception {
    // usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
    // usuarioService.salvar(usuario);
    // }

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

    @GetMapping()
    public List<Usuario> findAllUsuarios(Authentication authentication) {
        System.out.println("usuario logado: " + securityService.getUsuarioLogado().getNome());
        return usuarioService.findAll();
    }

}
