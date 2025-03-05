package com.uespi.reservalab.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.uespi.reservalab.authentication.CustomAuthentication;
import com.uespi.reservalab.models.Usuario;

@Component
public class SecurityService {

    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomAuthentication) {
            CustomAuthentication customAuthentication = (CustomAuthentication) authentication;
            return (Usuario) customAuthentication.getPrincipal();
        }

        return null;
    }

}
