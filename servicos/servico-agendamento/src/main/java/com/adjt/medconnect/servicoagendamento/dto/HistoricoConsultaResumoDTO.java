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
public class HistoricoConsultaResumoDTO {
    
    private Long id;
    private Long idConsulta;
    private String statusNovo;
    private LocalDateTime dataAlteracao;
}
