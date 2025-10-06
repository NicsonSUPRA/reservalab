package com.uespi.reservalab.controllers;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.uespi.reservalab.dto.CredentialsDTO;
import com.uespi.reservalab.dto.TokenDTO;
import com.uespi.reservalab.dto.UsuarioDTO;
import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.security.JwtService;
import com.uespi.reservalab.services.UsuarioService;
import com.uespi.reservalab.utils.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> cadastrarUsuario(
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

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Usuário cadastrado com sucesso!");
        response.put("location", location.toString());

        return ResponseEntity.created(location).body(response);
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

        // Atualiza o nome
        if (Utils.isNotEmpty(usuario.getNome())) {
            usuarioRetornado.setNome(usuario.getNome());
        }

        // Atualiza a senha (criptografada)
        if (Utils.isNotEmpty(usuario.getSenha())) {
            usuarioRetornado.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        usuarioService.atualizar(usuarioRetornado);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Buscar todos
    @GetMapping
    public ResponseEntity<List<Usuario>> findAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // 🔹 Buscar todos os professores e prof_comp
    @GetMapping("/professores")
    public ResponseEntity<List<Usuario>> findAllProfessores() {
        return ResponseEntity.ok(usuarioService.obterTodosUsuariosProfessoresAndProfComp());
    }

    // Controller
    @GetMapping("/pesquisar")
    public ResponseEntity<List<Usuario>> pesquisarUsuarios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) List<String> roles) {

        List<Usuario> usuarios = usuarioService.pesquisarUsuarios(nome, login, roles);
        return ResponseEntity.ok(usuarios);
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
