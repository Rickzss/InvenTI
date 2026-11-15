package com.inventi.inventidemo.repository;

import com.inventi.inventidemo.model.Usuario;
import com.inventi.inventidemo.model.Perfil;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByPerfil(Perfil perfil);

    long countByAtivoFalse();
}
