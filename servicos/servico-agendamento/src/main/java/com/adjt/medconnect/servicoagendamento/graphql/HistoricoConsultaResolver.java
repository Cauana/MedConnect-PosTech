package com.adjt.medconnect.servicoagendamento.graphql;

import com.adjt.medconnect.servicoagendamento.dto.HistoricoConsultaDTO;
import com.adjt.medconnect.servicoagendamento.dto.HistoricoConsultaResumoDTO;
import com.adjt.medconnect.servicoagendamento.dto.HistoricoConsultaDetalhadoDTO;
import com.adjt.medconnect.servicoagendamento.service.HistoricoConsultaService;
import com.adjt.medconnect.servicoagendamento.model.TipoAcao;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HistoricoConsultaResolver {
    
    private final HistoricoConsultaService historicoService;
    
    public HistoricoConsultaResolver(HistoricoConsultaService historicoService) {
        this.historicoService = historicoService;
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsultaDTO> historicoConsulta(@Argument Long idConsulta) {
        validarAcesso(idConsulta, null, false);
        return historicoService.obterHistoricoConsulta(idConsulta);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsultaDTO> historicoPaciente(@Argument Long idPaciente) {
        validarAcessoPaciente(idPaciente);
        return historicoService.obterHistoricoPaciente(idPaciente);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO')")
    public List<HistoricoConsultaDTO> historicoMedico(@Argument Long idMedico) {
        return historicoService.obterHistoricoMedico(idMedico);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsultaDTO> historicoConsultaPorPeriodo(
            @Argument Long idConsulta,
            @Argument String dataInicio,
            @Argument String dataFim) {
        validarAcesso(idConsulta, null, false);
        LocalDateTime inicio = LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime fim = LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_DATE_TIME);
        return historicoService.obterHistoricoConsultaPorPeriodo(idConsulta, inicio, fim);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsultaDTO> historicoPacientePorPeriodo(
            @Argument Long idPaciente,
            @Argument String dataInicio,
            @Argument String dataFim) {
        validarAcessoPaciente(idPaciente);
        LocalDateTime inicio = LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime fim = LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_DATE_TIME);
        return historicoService.obterHistoricoPacientePorPeriodo(idPaciente, inicio, fim);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO')")
    public List<HistoricoConsultaDTO> historicoMedicoPorPeriodo(
            @Argument Long idMedico,
            @Argument String dataInicio,
            @Argument String dataFim) {
        LocalDateTime inicio = LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime fim = LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_DATE_TIME);
        return historicoService.obterHistoricoMedicoPorPeriodo(idMedico, inicio, fim);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsultaResumoDTO> historicoConsultaResumo(@Argument Long idConsulta) {
        validarAcesso(idConsulta, null, false);
        return historicoService.obterHistoricoConsultaResumo(idConsulta);
    }
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsultaDetalhadoDTO> historicoConsultaDetalhado(@Argument Long idConsulta) {
        validarAcesso(idConsulta, null, false);
        return historicoService.obterHistoricoConsultaDetalhado(idConsulta);
    }
    
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMEIRO')")
    public HistoricoConsultaDTO registrarHistorico(
            @Argument Long idConsulta,
            @Argument Long idPaciente,
            @Argument Long idMedico,
            @Argument String statusAnterior,
            @Argument String statusNovo,
            @Argument String tipoAcao,
            @Argument Long idUsuarioResponsavel,
            @Argument String tipoUsuarioResponsavel,
            @Argument String descricao) {
        
        // Valida acesso para médicos
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = extrairPerfil(auth);
        Long idUsuarioAutenticado = extrairIdUsuario(auth);
        
        // Apenas o médico da consulta ou admin pode registrar histórico
        if ("ROLE_MEDICO".equals(userRole) && !idMedico.equals(idUsuarioAutenticado)) {
            throw new SecurityException("Médico não autorizado a registrar histórico de consulta de outro médico");
        }
        
        historicoService.registrarHistorico(
            idConsulta,
            idPaciente,
            idMedico,
            com.adjt.medconnect.servicoagendamento.model.StatusConsulta.valueOf(statusAnterior),
            com.adjt.medconnect.servicoagendamento.model.StatusConsulta.valueOf(statusNovo),
            TipoAcao.valueOf(tipoAcao),
            idUsuarioResponsavel,
            tipoUsuarioResponsavel,
            descricao
        );
        
        return historicoService.obterHistoricoConsulta(idConsulta).get(0);
    }
    
    private void validarAcesso(Long idConsulta, Long idPaciente, boolean isPaciente) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = extrairPerfil(auth);
        
        if ("ROLE_PACIENTE".equals(userRole)) {
            Long idUsuario = extrairIdUsuario(auth);
            historicoService.validarAcessoPaciente(idUsuario, idPaciente != null ? idPaciente : obterIdPacienteConsulta(idConsulta));
        } else if ("ROLE_MEDICO".equals(userRole)) {
            Long idMedico = extrairIdUsuario(auth);
            historicoService.validarAcessoMedico(idMedico, idConsulta);
        }
    }
    
    private void validarAcessoPaciente(Long idPaciente) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = extrairPerfil(auth);
        
        if ("ROLE_PACIENTE".equals(userRole)) {
            Long idUsuario = extrairIdUsuario(auth);
            historicoService.validarAcessoPaciente(idUsuario, idPaciente);
        }
    }
    
    private String extrairPerfil(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .findFirst()
                .orElse("");
    }
    
    private Long extrairIdUsuario(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof java.util.Map) {
            java.util.Map<String, Object> claims = (java.util.Map<String, Object>) principal;
            Object userId = claims.get("sub");
            if (userId != null) {
                try {
                    return Long.parseLong(userId.toString());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Erro ao extrair ID do usuário do token JWT");
                }
            }
        }
        throw new RuntimeException("Não foi possível extrair o ID do usuário do contexto de segurança");
    }

    private Long obterIdPacienteConsulta(Long idConsulta) {
        // Esta implementação seria integrada com o ConsultaRepository
        // Por enquanto, retornar null para ser tratado no serviço
        return null;
    }
}
