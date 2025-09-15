package com.uespi.reservalab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.uespi.reservalab.security.CustomUserDetailsService;
import com.uespi.reservalab.security.JwtAuthorizationFilter;
import com.uespi.reservalab.services.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> {
                    authorize.antMatchers("/usuarios/auth").permitAll();

                    authorize.antMatchers(HttpMethod.GET, "/usuarios/**")
                            .hasAnyRole("FUNCIONARIO", "ALUNO", "PROF_COMP", "PROF", "ADMIN");
                    authorize.antMatchers("/usuarios/**").hasRole("ADMIN");

                    authorize.antMatchers(HttpMethod.GET, "/laboratorios/**")
                            .hasAnyRole("FUNCIONARIO", "ALUNO", "PROF_COMP", "PROF", "ADMIN");
                    authorize.antMatchers("/laboratorios/**").hasRole("ADMIN");

                    authorize.antMatchers(HttpMethod.GET, "/semestre/**")
                            .hasAnyRole("FUNCIONARIO", "ALUNO", "PROF_COMP", "PROF", "ADMIN");
                    authorize.antMatchers("/semestre/**").hasRole("ADMIN");

                    authorize.anyRequest().authenticated();
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) {
        return new CustomUserDetailsService(usuarioService);
    }

    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new JwtAuthorizationFilter();
    }
}
