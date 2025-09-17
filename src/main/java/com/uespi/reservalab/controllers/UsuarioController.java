package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.uespi.reservalab.dto.CredentialsDTO;
import com.uespi.reservalab.dto.TokenDTO;
import com.uespi.reservalab.dto.UsuarioDTO;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.security.JwtService;
import com.uespi.reservalab.services.UsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 🔹 Autenticar
    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO autenticar(@RequestBody CredentialsDTO credential) {
        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(
                    credential.getUsername(),
                    credential.getPassword());
            String token = jwtService.generateToken(usuarioAutenticado);
            return new TokenDTO(usuarioAutenticado.getLogin(), token);
        } catch (Exception e) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }
    }

    // 🔹 Cadastro genérico por role
    @PostMapping("/cadastrar/{role}")
    public ResponseEntity<Void> cadastrarUsuario(
            @PathVariable("role") String role,
            @RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setLogin(usuarioDTO.getLogin());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuario.setRoles(Collections.singletonList(role.toUpperCase()));

        usuarioService.salvar(usuario);

        // URI do usuário criado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/usuarios/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // 🔹 Atualizar usuário
    @PutMapping
    public ResponseEntity<Void> atualizarUsuario(
            @RequestParam("id") String id,
            @RequestBody UsuarioDTO usuario) {
        Usuario usuarioRetornado = usuarioService.obterUsuarioPorId(UUID.fromString(id));

        if (usuarioRetornado == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioRetornado.setNome(usuario.getNome());
        usuarioService.atualizar(usuarioRetornado);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Buscar todos
    @GetMapping
    public ResponseEntity<List<Usuario>> findAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // 🔹 Buscar por ID
    @GetMapping("{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        Usuario usuario = usuarioService.obterUsuarioPorId(uuid);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    // 🔹 Buscar por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Usuario>> buscarPorNome(@PathVariable("nome") String nome) {
        return ResponseEntity.ok(usuarioService.obterUsuarioComNomeSemelhante(nome));
    }
}
