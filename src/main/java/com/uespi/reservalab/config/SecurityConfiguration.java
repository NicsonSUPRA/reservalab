package com.uespi.reservalab.config;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*")); // qualquer origem
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true); // necessário se usar cookies
                    return config;
                }))

                .authorizeHttpRequests(authorize -> {

                    // Autenticação liberada
                    authorize.antMatchers("/usuarios/auth").permitAll();

                    // 🔹 GETs de todos os recursos → todos os usuários autenticados podem ver
                    authorize.antMatchers(HttpMethod.GET, "/usuarios/**").hasAnyRole("ALUNO", "FUNCIONARIO", "PROF",
                            "PROF_COMP", "ADMIN");
                    authorize.antMatchers(HttpMethod.GET, "/laboratorios/**").hasAnyRole("ALUNO", "FUNCIONARIO", "PROF",
                            "PROF_COMP", "ADMIN");
                    authorize.antMatchers(HttpMethod.GET, "/semestre/**").hasAnyRole("ALUNO", "FUNCIONARIO", "PROF",
                            "PROF_COMP", "ADMIN");
                    authorize.antMatchers(HttpMethod.GET, "/reserva/**").hasAnyRole("ALUNO", "FUNCIONARIO", "PROF",
                            "PROF_COMP", "ADMIN");

                    // 🔹 POST/PUT/DELETE → apenas ADMIN ou professores conforme regra
                    authorize.antMatchers("/reserva/**").hasAnyRole("PROF", "PROF_COMP", "ADMIN"); // criar reservas
                                                                                                   // normais
                    authorize.antMatchers("/usuarios/**").hasRole("ADMIN");
                    authorize.antMatchers("/laboratorios/**").hasRole("ADMIN");
                    authorize.antMatchers("/semestre/**").hasRole("ADMIN");

                    // Qualquer outro endpoint → autenticado
                    authorize.anyRequest().authenticated();
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // Remove prefix "ROLE_"
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
