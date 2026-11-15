package com.inventi.inventidemo.model;

public enum StatusSolicitacao {

    PENDENTE("Pendente", "status-pendente", "Reabrir"),
    APROVADO("Aprovado", "status-ativo", "Aprovar"),
    EM_ATENDIMENTO("Em atendimento", "status-atendimento", "Em atendimento"),
    CONCLUIDO("Concluído", "status-concluido", "Concluir"),
    RECUSADO("Recusado", "status-inativo", "Recusar");

    private final String rotulo;
    private final String classeCss;
    private final String acao; // texto do botão do administrador

    StatusSolicitacao(String rotulo, String classeCss, String acao) {
        this.rotulo = rotulo;
        this.classeCss = classeCss;
        this.acao = acao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getClasseCss() {
        return classeCss;
    }

    public String getAcao() {
        return acao;
    }
}
