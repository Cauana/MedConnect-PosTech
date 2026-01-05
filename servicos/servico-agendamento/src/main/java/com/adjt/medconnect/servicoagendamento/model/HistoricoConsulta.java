package com.adjt.medconnect.servicoagendamento.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_consultas", indexes = {
    @Index(name = "idx_historico_consulta_id", columnList = "id_consulta"),
    @Index(name = "idx_historico_paciente_id", columnList = "id_paciente"),
    @Index(name = "idx_historico_medico_id", columnList = "id_medico"),
    @Index(name = "idx_historico_data_alteracao", columnList = "data_alteracao")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"consulta"})
@EqualsAndHashCode(exclude = {"consulta"})
public class HistoricoConsulta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "id_consulta")
    private Long idConsulta;
    
    @Column(nullable = false, name = "id_paciente")
    private Long idPaciente;
    
    @Column(nullable = false, name = "id_medico")
    private Long idMedico;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status_anterior")
    private StatusConsulta statusAnterior;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status_novo")
    private StatusConsulta statusNovo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_acao")
    private TipoAcao tipoAcao;
    
    @Column(nullable = false, name = "id_usuario_responsavel")
    private Long idUsuarioResponsavel;
    
    @Column(nullable = false, name = "tipo_usuario_responsavel")
    private String tipoUsuarioResponsavel;
    
    @Column(columnDefinition = "TEXT", name = "descricao")
    private String descricao;
    
    @Column(nullable = false, name = "data_alteracao")
    private LocalDateTime dataAlteracao;
    
    @Column(nullable = false, updatable = false, name = "criado_em")
    private LocalDateTime criadoEm;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta", insertable = false, updatable = false)
    private Consulta consulta;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (dataAlteracao == null) {
            dataAlteracao = LocalDateTime.now();
        }
    }
}
