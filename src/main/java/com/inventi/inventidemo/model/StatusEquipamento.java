package com.inventi.inventidemo.model;

public enum StatusEquipamento {

    ATIVO("Ativo", "status-ativo"),
    EM_MANUTENCAO("Em manutenção", "status-manutencao"),
    INATIVO("Inativo", "status-inativo");

    private final String rotulo;
    private final String classeCss;

    StatusEquipamento(String rotulo, String classeCss) {
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
