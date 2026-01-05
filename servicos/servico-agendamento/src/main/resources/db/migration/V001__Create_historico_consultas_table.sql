-- V001__Create_historico_consultas_table.sql

CREATE TABLE IF NOT EXISTS historico_consultas (
    id BIGSERIAL PRIMARY KEY,
    id_consulta BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    id_medico BIGINT NOT NULL,
    status_anterior VARCHAR(50) NOT NULL,
    status_novo VARCHAR(50) NOT NULL,
    tipo_acao VARCHAR(50) NOT NULL,
    id_usuario_responsavel BIGINT NOT NULL,
    tipo_usuario_responsavel VARCHAR(50) NOT NULL,
    descricao TEXT,
    data_alteracao TIMESTAMP NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_consulta FOREIGN KEY (id_consulta) REFERENCES consultas(id) ON DELETE CASCADE
);

-- Criar índices para melhor performance
CREATE INDEX idx_historico_consulta_id ON historico_consultas(id_consulta);
CREATE INDEX idx_historico_paciente_id ON historico_consultas(id_paciente);
CREATE INDEX idx_historico_medico_id ON historico_consultas(id_medico);
CREATE INDEX idx_historico_data_alteracao ON historico_consultas(data_alteracao);
CREATE INDEX idx_historico_tipo_acao ON historico_consultas(tipo_acao);
