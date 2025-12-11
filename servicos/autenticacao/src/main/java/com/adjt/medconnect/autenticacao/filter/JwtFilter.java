package com.adjt.medconnect.autenticacao.filter;

import com.adjt.medconnect.autenticacao.service.JwtService;
import com.adjt.medconnect.autenticacao.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtFilter(JwtService jwtService, UsuarioService usuarioService){
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException{
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(header != null&& header.startsWith("Bearer ")){
            String token = header.substring(7);
            String username = jwtService.validarToken(token);

            if(username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null){
                var userDetails = usuarioService.loadUserByUsername(username);
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request,response);
    }
}
