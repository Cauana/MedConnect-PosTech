package com.adjt.medconnect.servicoagendamento.graphql;

import com.adjt.medconnect.servicoagendamento.dto.HistoricoConsultaDTO;
import com.adjt.medconnect.servicoagendamento.dto.HistoricoConsultaResumoDTO;
import com.adjt.medconnect.servicoagendamento.service.HistoricoConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes para o GraphQL Resolver de Histórico de Consultas
 */
@GraphQlTest(HistoricoConsultaResolver.class)
public class HistoricoConsultaResolverTest {
    
    @Autowired
    private GraphQlTester graphQlTester;
    
    @MockBean
    private HistoricoConsultaService historicoService;
    
    @BeforeEach
    void setUp() {
        // Setup mocks if needed
    }
    
    @Test
    void testHistoricoConsultaQuery() {
        // Arrange
        HistoricoConsultaDTO dto1 = HistoricoConsultaDTO.builder()
                .id(1L)
                .idConsulta(1L)
                .idPaciente(1L)
                .idMedico(1L)
                .statusAnterior("AGENDADA")
                .statusNovo("CONFIRMADA")
                .tipoAcao("ALTERACAO_DE_STATUS")
                .idUsuarioResponsavel(1L)
                .tipoUsuarioResponsavel("MEDICO")
                .descricao("Status alterado")
                .dataAlteracao(LocalDateTime.now())
                .build();
        
        when(historicoService.obterHistoricoConsulta(any()))
                .thenReturn(Arrays.asList(dto1));
        
        // Act & Assert
        graphQlTester.document("""
                query {
                    historicoConsulta(idConsulta: 1) {
                        id
                        idConsulta
                        statusAnterior
                        statusNovo
                        tipoAcao
                        dataAlteracao
                    }
                }
                """)
                .execute()
                .path("historicoConsulta")
                .entityList(HistoricoConsultaDTO.class)
                .hasSize(1)
                .satisfies(list -> {
                    HistoricoConsultaDTO item = list.get(0);
                    assert item.getIdConsulta().equals(1L);
                    assert item.getStatusNovo().equals("CONFIRMADA");
                });
    }
    
    @Test
    void testHistoricoConsultaResumoQuery() {
        // Arrange
        HistoricoConsultaResumoDTO dto1 = HistoricoConsultaResumoDTO.builder()
                .id(1L)
                .idConsulta(1L)
                .statusNovo("CONFIRMADA")
                .dataAlteracao(LocalDateTime.now())
                .build();
        
        when(historicoService.obterHistoricoConsultaResumo(any()))
                .thenReturn(Arrays.asList(dto1));
        
        // Act & Assert
        graphQlTester.document("""
                query {
                    historicoConsultaResumo(idConsulta: 1) {
                        id
                        idConsulta
                        statusNovo
                        dataAlteracao
                    }
                }
                """)
                .execute()
                .path("historicoConsultaResumo")
                .entityList(HistoricoConsultaResumoDTO.class)
                .hasSize(1);
    }
}
