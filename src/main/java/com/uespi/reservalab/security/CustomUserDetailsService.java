package com.uespi.reservalab.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.obterUsuarioPorLogin(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("usuario não encontrado");
        }

        return User
                .builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities(usuario.getAuthorities().toArray(new String[usuario.getAuthorities().size()]))
                .build();
    }

}
