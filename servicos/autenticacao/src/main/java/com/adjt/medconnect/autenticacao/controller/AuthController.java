package com.adjt.medconnect.autenticacao.controller;

import com.adjt.medconnect.autenticacao.dto.LoginDTO;
import com.adjt.medconnect.autenticacao.dto.TokenDTO;
import com.adjt.medconnect.autenticacao.model.Role;
import com.adjt.medconnect.autenticacao.model.Usuario;
import com.adjt.medconnect.autenticacao.repository.UsuarioRepository;
import com.adjt.medconnect.autenticacao.service.JwtService;
import com.adjt.medconnect.autenticacao.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Usuario usuario) {
        usuario.setRole(Role.PACIENTE); // força role segura
        usuarioService.salvar(usuario);

        return ResponseEntity.created(
                URI.create("/auth/" + usuario.getId())
        ).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );

        String token = jwtService.gerarToken(authentication);
        

        return ResponseEntity.ok(new TokenDTO(token));
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication auth) {
        return ResponseEntity.ok("Você está autenticado: " + auth.getName());
    }
}
