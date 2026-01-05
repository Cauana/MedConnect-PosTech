package com.adjt.medconnect.servicohistorico.repository;

import com.adjt.medconnect.servicohistorico.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByIdPaciente(Long idPaciente);
    List<Consulta> findByIdMedico(Long idMedico);
}
