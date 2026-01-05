package com.adjt.medconnect.servicohistorico.controller;

import com.adjt.medconnect.servicohistorico.model.Consulta;
import com.adjt.medconnect.servicohistorico.repository.ConsultaRepository;
import com.adjt.medconnect.servicohistorico.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class HistoricoController {

    private final ConsultaRepository repository;
    private final JwtService jwtService;

    @QueryMapping
    public List<Consulta> historicoPaciente(@Argument Long idPaciente, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PACIENTE"));
        if (isPaciente) {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new AccessDeniedException("Token ausente");
            }
            String token = header.substring(7);
            Long userId = jwtService.extractUserId(token);
            if (userId == null || !userId.equals(idPaciente)) {
                throw new AccessDeniedException("Paciente só pode acessar seu próprio histórico");
            }
        }
        return repository.findByIdPaciente(idPaciente);
    }

    @QueryMapping
    public List<Consulta> consultasMedico(@Argument Long idMedico, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isMedico = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEDICO"));
        boolean isEnfermeiro = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ENFERMEIRO"));
        if (!(isMedico || isEnfermeiro)) {
            throw new AccessDeniedException("Acesso restrito ao histórico do médico");
        }
        return repository.findByIdMedico(idMedico);
    }
}
