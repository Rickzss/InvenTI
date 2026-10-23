package com.inventi.inventidemo.repository;

import com.inventi.inventidemo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Os métodos CRUD (findAll, save, deleteById...) já vêm prontos
}
