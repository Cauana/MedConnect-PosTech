package com.adjt.medconnect.servicoagendamento.model;

public enum TipoAcao {
    CRIACAO("Criação de consulta"),
    EDICAO("Edição de consulta"),
    ALTERACAO_DE_STATUS("Alteração de status"),
    CANCELAMENTO("Cancelamento de consulta"),
    REAGENDAMENTO("Reagendamento de consulta"),
    CONFIRMACAO("Confirmação de consulta");

    private final String descricao;

    TipoAcao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
