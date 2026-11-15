package com.inventi.inventidemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventi.inventidemo.config.UsuarioSessao;
import com.inventi.inventidemo.model.Perfil;
import com.inventi.inventidemo.model.Usuario;
import com.inventi.inventidemo.repository.UsuarioRepository;
import com.inventi.inventidemo.util.SenhaUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    /** Nome do atributo de sessão com o usuário autenticado. */
    public static final String SESSAO = "usuarioLogado";

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Raiz do site: o interceptor já garantiu que há alguém logado (administrador)
    @GetMapping("/")
    public String raiz() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String formulario(HttpSession sessao) {
        if (sessao.getAttribute(SESSAO) instanceof UsuarioSessao logado) {
            return "redirect:" + destinoInicial(logado.getPerfil());
        }
        return "login";
    }

    // Autentica e redireciona conforme o perfil:
    // ADMINISTRADOR -> /home | USUARIO (solicitante) -> /solicitacao
    @PostMapping("/login")
    public String autenticar(@RequestParam String email, @RequestParam String senha,
            HttpServletRequest request, RedirectAttributes redirect) {

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim()).orElse(null);

        if (usuario == null || !usuario.isAtivo() || !SenhaUtil.confere(senha, usuario.getSenha())) {
            redirect.addFlashAttribute("erro", "E-mail ou senha inválidos.");
            return "redirect:/login";
        }

        HttpSession sessao = request.getSession(true);
        request.changeSessionId(); // evita fixação de sessão
        sessao.setAttribute(SESSAO, new UsuarioSessao(usuario));
        return "redirect:" + destinoInicial(usuario.getPerfil());
    }

    @GetMapping("/logout")
    public String sair(HttpSession sessao) {
        sessao.invalidate();
        return "redirect:/login";
    }

    private static String destinoInicial(Perfil perfil) {
        return perfil == Perfil.ADMINISTRADOR ? "/home" : "/solicitacao";
    }
}
