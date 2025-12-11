package com.adjt.medconnect.autenticacao.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private static final long EXPIRATION = 1000 * 60 * 60; // 1hora
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String gerarToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("MedConnect")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String validarToken(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }catch (Exception e){
            return null;
        }
    }

}
