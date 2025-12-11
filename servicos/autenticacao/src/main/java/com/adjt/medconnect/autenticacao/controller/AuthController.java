package com.adjt.medconnect.autenticacao.controller;

import com.adjt.medconnect.autenticacao.model.Usuario;
import com.adjt.medconnect.autenticacao.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PasswordEncoder encoder;
    private final UsuarioRepository repository;

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario){
        if(repository.findByUsername(usuario.getUsername()).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        Usuario saved = repository.save(usuario);
        return ResponseEntity.created(URI.create("/auth" + saved.getId())).body(saved);
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(){
        return ResponseEntity.ok("Você está autenticado");
    }
}
