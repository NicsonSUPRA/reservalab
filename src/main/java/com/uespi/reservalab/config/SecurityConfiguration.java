package com.uespi.reservalab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.uespi.reservalab.security.CustomUserDetailsService;
import com.uespi.reservalab.security.JwtCustomAuthenticationFilter;
import com.uespi.reservalab.services.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtCustomAuthenticationFilter jwtCustomAuthenticationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(configurer -> configurer.loginPage("/login"))
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/login/**").permitAll();
                    authorize.requestMatchers("/usuarios/**").hasAuthority("ADMIN");
                    authorize.requestMatchers("/clients/**").hasAuthority("ADMIN");
                    authorize.requestMatchers("/teste/admin").hasAuthority("ADMIN");
                    authorize.requestMatchers("/teste/professorComp").hasAuthority("PROF_COMP");
                    authorize.requestMatchers("/teste/professor").hasAuthority("PROF");
                    authorize.requestMatchers(HttpMethod.POST, "/laboratorios/**").hasAuthority("ADMIN");
                    authorize.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()))
                .addFilterAfter(jwtCustomAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) {
        return new CustomUserDetailsService(usuarioService);
    }

}
