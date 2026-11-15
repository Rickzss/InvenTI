package com.inventi.inventidemo.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.inventi.inventidemo.controller.LoginController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Disponibiliza ${usuarioLogado} em todos os templates (usado no menu lateral). */
@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("usuarioLogado")
    public UsuarioSessao usuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return null;
        }
        Object atributo = sessao.getAttribute(LoginController.SESSAO);
        return (atributo instanceof UsuarioSessao usuario) ? usuario : null;
    }
}
