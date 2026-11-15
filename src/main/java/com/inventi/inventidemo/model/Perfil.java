package com.inventi.inventidemo.model;

public enum Perfil {

    ADMINISTRADOR("Administrador", "status-atendimento"),
    USUARIO("Solicitante", "status-concluido");

    private final String rotulo;
    private final String classeCss;

    Perfil(String rotulo, String classeCss) {
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
