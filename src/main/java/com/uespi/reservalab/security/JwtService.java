package com.uespi.reservalab.security;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.uespi.reservalab.models.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

@Service
@Getter
public class JwtService {

    @Value("${security.jwt.expiration}")
    private String expiration;

    @Value("${security.jwt.signing-key}")
    private String signingKey;

    public String generateToken(Usuario usuario) {
        LocalDateTime dateExpiration = LocalDateTime.now().plusMinutes(Integer.parseInt(this.expiration));
        Date date = Date.from(dateExpiration.atZone(java.time.ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(usuario.getLogin())
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, this.signingKey)
                .claim("roles", usuario.getRoles())
                .compact();

    }

    public Claims getClaims(String token) throws ExpiredJwtException {
        return Jwts
                .parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getLoggedUser(String token) {
        try {
            Claims claims = getClaims(token);

            String login = claims.getSubject();

            return login;
        } catch (ExpiredJwtException e) {
            System.out.println("⚠️ Token expirado: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("⚠️ Token inválido: " + e.getMessage());
            return null;
        }
    }

}

/*
 * 🚀 Alternativa mais limpa: @ConfigurationProperties
 *
 * Em vez de usar @Value em cada campo, você pode mapear todos os valores
 * de uma vez com @ConfigurationProperties.
 *
 * Exemplo:
 *
 * @Service
 * 
 * @ConfigurationProperties(prefix = "security.jwt")
 * public class JwtService {
 *
 * private String expiration;
 * private String signingKey;
 *
 * public String getExpiration() { return expiration; }
 * public void setExpiration(String expiration) { this.expiration = expiration;
 * }
 *
 * public String getSigningKey() { return signingKey; }
 * public void setSigningKey(String signingKey) { this.signingKey = signingKey;
 * }
 * }
 *
 * 👉 Para usar essa versão, remova os @Value, descomente o código acima
 * e adicione a anotação @EnableConfigurationProperties no seu projeto
 * (geralmente em uma classe @SpringBootApplication).
 */
