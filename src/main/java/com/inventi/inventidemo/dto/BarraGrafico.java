package com.inventi.inventidemo.dto;

/** Uma barra dos gráficos do dashboard: rótulo, quantidade e largura (%) já calculada. */
public class BarraGrafico {

    private final String rotulo;
    private final long total;
    private final int percentual;

    public BarraGrafico(String rotulo, long total, int percentual) {
        this.rotulo = rotulo;
        this.total = total;
        this.percentual = percentual;
    }

    public String getRotulo() {
        return rotulo;
    }

    public long getTotal() {
        return total;
    }

    public int getPercentual() {
        return percentual;
    }
}
