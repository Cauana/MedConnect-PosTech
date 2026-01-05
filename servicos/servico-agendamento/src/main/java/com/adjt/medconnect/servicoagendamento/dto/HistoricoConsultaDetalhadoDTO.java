package com.adjt.medconnect.servicoagendamento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoConsultaDetalhadoDTO {
    
    private Long id;
    private Long idConsulta;
    private Long idPaciente;
    private Long idMedico;
    private String statusAnterior;
    private String statusNovo;
    private String tipoAcao;
    private Long idUsuarioResponsavel;
    private String tipoUsuarioResponsavel;
    private String descricao;
    private LocalDateTime dataAlteracao;
}
