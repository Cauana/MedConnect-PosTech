package com.adjt.medconnect.servicoagendamento.controller;

import org.springframework.web.bind.annotation.*;

import com.adjt.medconnect.servicoagendamento.model.Usuario;
import com.adjt.medconnect.servicoagendamento.model.TipoUsuario;
import com.adjt.medconnect.servicoagendamento.repository.UsuarioRepository;
import com.adjt.medconnect.servicoagendamento.security.JwtService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final JwtService jwtService;

    public UsuarioController(UsuarioRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody Usuario usuario, HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AccessDeniedException("Token ausente");
        }
        String token = header.substring(7);
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        if (role == null) {
            throw new AccessDeniedException("Role ausente no token");
        }
        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new AccessDeniedException("Somente ADMIN pode cadastrar usuários");
        }
        var salvo = repository.save(usuario);
        Map<String, Object> body = new HashMap<>();
        body.put("id", salvo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    
    @GetMapping
    public List<Usuario> listar() {
        return repository.findAll();
    }
}
