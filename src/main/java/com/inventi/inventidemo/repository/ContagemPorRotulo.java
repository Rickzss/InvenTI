package com.inventi.inventidemo.repository;

/** Resultado de uma consulta "agrupar e contar" (ex.: equipamentos por tipo ou por setor). */
public interface ContagemPorRotulo {

    String getRotulo();

    Long getTotal();
}
