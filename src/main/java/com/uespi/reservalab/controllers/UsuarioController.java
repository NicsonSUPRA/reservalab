package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.uespi.reservalab.annotations.ClientDefault;
import com.uespi.reservalab.dto.UsuarioDTO;
import com.uespi.reservalab.models.Client;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.services.ClientService;
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

    private final ClientService clientService;

    private final PasswordEncoder passwordEncoder;

    @ClientDefault
    private final Client clientDefault;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/admin")
    public void cadastrarUsuarioAdmin(@RequestBody UsuarioDTO usuarioDTO) throws Exception {
        Usuario usuario = new Usuario();
        Client client = clientDefault;

        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setAuthorities(Arrays.asList("ADMIN"));
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        if (clientService.obterClientByClientId(usuarioDTO.getClientId()) != null) {
            throw new Exception("Client já cadastrado");
        }
        client.setClientId(usuarioDTO.getClientId());
        client.setClientSecret(passwordEncoder.encode(usuarioDTO.getClientSecret()));

        usuario.setClient(client);
        usuarioService.salvar(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/professorComputacao")
    public void cadastrarUsuarioProfessorComputacao(@RequestBody UsuarioDTO usuarioDTO) throws Exception {
        Usuario usuario = new Usuario();
        Client client = clientDefault;

        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setAuthorities(Arrays.asList("PROF_COMP"));
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        if (clientService.obterClientByClientId(usuarioDTO.getClientId()) != null) {
            throw new Exception("Client já cadastrado");
        }
        client.setClientId(usuarioDTO.getClientId());
        client.setClientSecret(passwordEncoder.encode(usuarioDTO.getClientSecret()));

        usuario.setClient(client);
        usuarioService.salvar(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/professor")
    public void cadastrarUsuarioProfessor(@RequestBody UsuarioDTO usuarioDTO) throws Exception {
        Usuario usuario = new Usuario();
        Client client = clientDefault;

        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setAuthorities(Arrays.asList("PROF"));
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        if (clientService.obterClientByClientId(usuarioDTO.getClientId()) != null) {
            throw new Exception("Client já cadastrado");
        }
        client.setClientId(usuarioDTO.getClientId());
        client.setClientSecret(passwordEncoder.encode(usuarioDTO.getClientSecret()));

        usuario.setClient(client);
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
