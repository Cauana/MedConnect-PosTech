package com.adjt.medconnect.servicoagendamento.repository;

import com.adjt.medconnect.servicoagendamento.model.HistoricoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricoConsultaRepository extends JpaRepository<HistoricoConsulta, Long> {
    
    List<HistoricoConsulta> findByIdConsultaOrderByDataAlteracaoDesc(Long idConsulta);
    
    List<HistoricoConsulta> findByIdPacienteOrderByDataAlteracaoDesc(Long idPaciente);
    
    List<HistoricoConsulta> findByIdMedicoOrderByDataAlteracaoDesc(Long idMedico);
    
    @Query("SELECT h FROM HistoricoConsulta h WHERE h.idConsulta = :idConsulta " +
           "AND h.dataAlteracao BETWEEN :dataInicio AND :dataFim ORDER BY h.dataAlteracao DESC")
    List<HistoricoConsulta> findConsultaHistoricoByPeriodo(
        @Param("idConsulta") Long idConsulta,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
    
    @Query("SELECT h FROM HistoricoConsulta h WHERE h.idPaciente = :idPaciente " +
           "AND h.dataAlteracao BETWEEN :dataInicio AND :dataFim ORDER BY h.dataAlteracao DESC")
    List<HistoricoConsulta> findPacienteHistoricoByPeriodo(
        @Param("idPaciente") Long idPaciente,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    @Query("SELECT h FROM HistoricoConsulta h WHERE h.idMedico = :idMedico " +
           "AND h.dataAlteracao BETWEEN :dataInicio AND :dataFim ORDER BY h.dataAlteracao DESC")
    List<HistoricoConsulta> findMedicoHistoricoByPeriodo(
        @Param("idMedico") Long idMedico,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
}
