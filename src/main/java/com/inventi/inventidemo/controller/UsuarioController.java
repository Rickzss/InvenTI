package com.inventi.inventidemo.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventi.inventidemo.config.UsuarioSessao;
import com.inventi.inventidemo.model.Perfil;
import com.inventi.inventidemo.model.Usuario;
import com.inventi.inventidemo.repository.SetorRepository;
import com.inventi.inventidemo.repository.UsuarioRepository;
import com.inventi.inventidemo.util.SenhaUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsuarioController {

    private static final int SENHA_MINIMA = 6;

    private final UsuarioRepository usuarioRepository;
    private final SetorRepository setorRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, SetorRepository setorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.setorRepository = setorRepository;
    }

    // Lista de usuários + cards de resumo
    @GetMapping("/usuarios")
    public String listar(Model model) {
        return montarPagina(model, null);
    }

    // Recarrega a página com o painel de edição preenchido
    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios"; // id inexistente
        }
        return montarPagina(model, usuario);
    }

    @PostMapping("/usuarios/{id}/editar")
    public String atualizar(@PathVariable Long id, @RequestParam String nome, @RequestParam String email,
            @RequestParam(required = false) Long setorId, @RequestParam Perfil perfil,
            @RequestParam(required = false) String senha, HttpSession sessao, RedirectAttributes redirect) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        String voltar = "redirect:/usuarios/" + id + "/editar";

        if (nome.isBlank() || email.isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o nome e o e-mail.");
            return voltar;
        }
        Usuario outro = usuarioRepository.findByEmailIgnoreCase(email.trim()).orElse(null);
        if (outro != null && !outro.getId().equals(id)) {
            redirect.addFlashAttribute("erro", "O e-mail " + email.trim() + " já pertence a outro usuário.");
            return voltar;
        }
        if (senha != null && !senha.isEmpty() && senha.length() < SENHA_MINIMA) {
            redirect.addFlashAttribute("erro", "A nova senha precisa ter pelo menos " + SENHA_MINIMA + " caracteres.");
            return voltar;
        }

        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim());
        usuario.setSetor(setorId == null ? null : setorRepository.findById(setorId).orElse(null));
        // Ninguém rebaixa o próprio perfil (evita ficar sem acesso administrativo)
        if (!ehUsuarioLogado(sessao, id)) {
            usuario.setPerfil(perfil);
        }
        if (senha != null && !senha.isEmpty()) {
            usuario.setSenha(SenhaUtil.gerarHash(senha));
        }
        usuarioRepository.save(usuario);

        redirect.addFlashAttribute("mensagem", "Usuário " + usuario.getNome() + " atualizado.");
        return "redirect:/usuarios";
    }

    // Inativa ou reativa (não exclui: preserva o histórico de solicitações)
    @PostMapping("/usuarios/{id}/alternar-status")
    public String alternarStatus(@PathVariable Long id, HttpSession sessao, RedirectAttributes redirect) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        if (ehUsuarioLogado(sessao, id)) {
            redirect.addFlashAttribute("erro", "Você não pode inativar o próprio usuário.");
            return "redirect:/usuarios";
        }
        usuario.setAtivo(!usuario.isAtivo());
        usuarioRepository.save(usuario);

        redirect.addFlashAttribute("mensagem",
                "Usuário " + usuario.getNome() + (usuario.isAtivo() ? " reativado." : " inativado."));
        return "redirect:/usuarios";
    }

    private String montarPagina(Model model, Usuario edicao) {
        model.addAttribute("usuarios", usuarioRepository.findAll(Sort.by("nome")));
        model.addAttribute("total", usuarioRepository.count());
        model.addAttribute("administradores", usuarioRepository.countByPerfil(Perfil.ADMINISTRADOR));
        model.addAttribute("solicitantes", usuarioRepository.countByPerfil(Perfil.USUARIO));
        model.addAttribute("inativos", usuarioRepository.countByAtivoFalse());
        model.addAttribute("setores", setorRepository.findAllByOrderByNomeAsc());
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("edicao", edicao);
        return "usuarios";
    }

    private static boolean ehUsuarioLogado(HttpSession sessao, Long id) {
        return sessao.getAttribute(LoginController.SESSAO) instanceof UsuarioSessao logado
                && logado.getId().equals(id);
    }
}
