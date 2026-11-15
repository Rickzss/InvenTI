package com.inventi.inventidemo.repository;

import com.inventi.inventidemo.model.Equipamento;
import com.inventi.inventidemo.model.StatusEquipamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    boolean existsByPatrimonioIgnoreCase(String patrimonio);

    List<Equipamento> findAllByOrderByDataCadastroDesc();

    // Filtro da tela Equipamentos: nome contendo o texto, sem diferenciar maiúsculas/minúsculas
    List<Equipamento> findByNomeContainingIgnoreCaseOrderByDataCadastroDesc(String nome);

    // Dashboard
    long countByStatus(StatusEquipamento status);

    List<Equipamento> findTop5ByOrderByDataCadastroDescIdDesc();

    @Query("select e.categoria.nome as rotulo, count(e) as total "
            + "from Equipamento e group by e.categoria.nome "
            + "order by count(e) desc, e.categoria.nome asc")
    List<ContagemPorRotulo> contarPorCategoria();

    // left join: equipamentos sem setor também entram na contagem (rótulo nulo)
    @Query("select s.nome as rotulo, count(e) as total "
            + "from Equipamento e left join e.setor s group by s.nome "
            + "order by count(e) desc, s.nome asc")
    List<ContagemPorRotulo> contarPorSetor();
}
