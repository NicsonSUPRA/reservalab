package com.uespi.reservalab.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.uespi.reservalab.models.Usuario;
import com.uespi.reservalab.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UsuarioService usuarioService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Usuario usuario = usuarioService.obterUsuarioPorLogin(authentication.getName());

        if (usuario == null) {
            throw new UsernameNotFoundException("usuario e/ou senha inválidos");
        }

        if (passwordEncoder.matches(authentication.getCredentials().toString(), usuario.getSenha())) {
            return new CustomAuthentication(usuario);
        }
        throw new UsernameNotFoundException("usuario e/ou senha inválidos");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

}
