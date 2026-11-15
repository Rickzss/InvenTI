package com.inventi.inventidemo.repository;

import com.inventi.inventidemo.model.Setor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetorRepository extends JpaRepository<Setor, Long> {
    List<Setor> findAllByOrderByNomeAsc();

    Optional<Setor> findByNome(String nome);
}
