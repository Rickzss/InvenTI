package com.inventi.inventidemo.repository;

import com.inventi.inventidemo.model.Solicitacao;
import com.inventi.inventidemo.model.StatusSolicitacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    List<Solicitacao> findBySolicitanteIdOrderByDataSolicitacaoDesc(Long usuarioId);

    // Painel do administrador
    List<Solicitacao> findAllByOrderByDataSolicitacaoDesc();

    List<Solicitacao> findByStatusOrderByDataSolicitacaoDesc(StatusSolicitacao status);

    long countByStatus(StatusSolicitacao status);
}
