package com.inventi.inventidemo.model;

public enum Urgencia {

    BAIXA("Baixa", "status-concluido"),
    MEDIA("Média", "status-pendente"),
    ALTA("Alta", "status-inativo");

    private final String rotulo;
    private final String classeCss;

    Urgencia(String rotulo, String classeCss) {
        this.rotulo = rotulo;
        this.classeCss = classeCss;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getClasseCss() {
        return classeCss;
    }
}
