package com.inventi.inventidemo.config;

import java.io.Serializable;

import com.inventi.inventidemo.model.Perfil;
import com.inventi.inventidemo.model.Usuario;

/** Dados mínimos do usuário guardados na sessão HTTP (nunca a senha). */
public class UsuarioSessao implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String nome;
    private final Perfil perfil;

    public UsuarioSessao(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.perfil = usuario.getPerfil();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public boolean isAdministrador() {
        return perfil == Perfil.ADMINISTRADOR;
    }
}
