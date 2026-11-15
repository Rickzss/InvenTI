package com.inventi.inventidemo.config;

import java.io.IOException;

import org.springframework.web.servlet.HandlerInterceptor;

import com.inventi.inventidemo.controller.LoginController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controle de acesso por perfil:
 *  - sem login             -> /login
 *  - Solicitante (USUARIO) -> somente /solicitacao (nunca vê o menu administrativo)
 *  - Administrador         -> todas as telas administrativas (/solicitacao é só do solicitante)
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        String contexto = request.getContextPath();
        String caminho = request.getRequestURI().substring(contexto.length());

        HttpSession sessao = request.getSession(false);
        Object atributo = (sessao == null) ? null : sessao.getAttribute(LoginController.SESSAO);

        if (!(atributo instanceof UsuarioSessao usuario)) {
            response.sendRedirect(contexto + "/login");
            return false;
        }

        boolean rotaSolicitante = caminho.equals("/solicitacao") || caminho.startsWith("/solicitacao/");

        if (!usuario.isAdministrador() && !rotaSolicitante) {
            response.sendRedirect(contexto + "/solicitacao");
            return false;
        }
        if (usuario.isAdministrador() && rotaSolicitante) {
            response.sendRedirect(contexto + "/home");
            return false;
        }
        return true;
    }
}
