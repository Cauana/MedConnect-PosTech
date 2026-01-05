package com.adjt.medconnect.servicoagendamento.controller;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import com.adjt.medconnect.servicoagendamento.model.Consulta;
import com.adjt.medconnect.servicoagendamento.model.StatusConsulta;
import com.adjt.medconnect.servicoagendamento.repository.ConsultaRepository;
import com.adjt.medconnect.servicoagendamento.repository.UsuarioRepository;
import com.adjt.medconnect.servicoagendamento.service.ConsultaService;
import com.adjt.medconnect.servicoagendamento.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "Endpoints para gerenciamento de consultas médicas")
 public class ConsultaController {

    private final ConsultaRepository repositorio;

    private final ConsultaService service;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public ConsultaController(ConsultaRepository repositorio, ConsultaService service, JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.repositorio = repositorio;
        this.service = service;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @Operation(
        summary = "Criar nova consulta",
        description = "Cria uma nova consulta e envia notificação através do Kafka"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta criada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Consulta.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public Consulta criar(@RequestBody Consulta consulta, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PACIENTE"));

        if (isPaciente) {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new AccessDeniedException("Token ausente");
            }
            String token = header.substring(7);
            String username = jwtService.extractUsername(token);
            var usuarioPaciente = usuarioRepository.findByEmail(username);
            if (usuarioPaciente == null || usuarioPaciente.getId() != consulta.getIdPaciente()) {
                throw new AccessDeniedException("Paciente só pode criar consulta para si próprio");
            }
        }
        return service.agendarConsulta(consulta);
    }

    @GetMapping
    @Operation(
        summary = "Listar todas as consultas",
        description = "Retorna uma lista de todas as consultas cadastradas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de consultas retornada com sucesso")
    public List<Consulta> listar(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PACIENTE"));
        if (isPaciente) {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new AccessDeniedException("Token ausente");
            }
            String token = header.substring(7);
            String username = jwtService.extractUsername(token);
            var usuarioPaciente = usuarioRepository.findByEmail(username);
            if (usuarioPaciente == null) {
                throw new AccessDeniedException("Identidade inválida");
            }
            return repositorio.findByIdPaciente(usuarioPaciente.getId());
        }
        return repositorio.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar consulta por ID",
        description = "Retorna uma consulta específica pelo seu ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public Consulta buscarPorId(@PathVariable long id) {
        return repositorio.findById(id).orElse(null);
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Atualizar status da consulta",
        description = "Atualiza o status de uma consulta existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public Consulta atualizarStatus(@PathVariable long id, @RequestParam String status, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isMedico = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEDICO"));
        if (!isMedico) {
            throw new AccessDeniedException("Somente médicos podem atualizar status de consulta");
        }
        var consulta = repositorio.findById(id).orElse(null);
        if (consulta != null) {
            consulta.setStatus(Enum.valueOf(StatusConsulta.class, status.toUpperCase()));
            return repositorio.save(consulta);
        }
        return null;
    }
}
