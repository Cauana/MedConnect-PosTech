package com.adjt.medconnect.servicoagendamento.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtService jwtService;

    public Authentication validateToken(String token) {
        log.debug("Validando token JWT...");

        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Token não pode ser nulo ou vazio");
        }

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token inválido ou expirado");
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        log.info("Token válido - Username: {}, Role: {}", username, role);

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
        );

        log.info("Authorities adicionadas: {}", authorities);

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
        );
    }

    public String extractUsername(String token) {
        return jwtService.extractUsername(token);
    }

    public String extractRole(String token) {
        return jwtService.extractRole(token);
    }

    public Authentication validateTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Formato de Authorization header inválido. Esperado: Bearer <token>");
        }

        String token = authHeader.substring(7);
        return validateToken(token);
    }

    public String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Formato de Authorization header inválido");
        }
        return authHeader.substring(7);
    }
}
