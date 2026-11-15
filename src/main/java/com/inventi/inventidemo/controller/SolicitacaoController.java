package com.inventi.inventidemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventi.inventidemo.config.UsuarioSessao;
import com.inventi.inventidemo.model.Categoria;
import com.inventi.inventidemo.model.Setor;
import com.inventi.inventidemo.model.Solicitacao;
import com.inventi.inventidemo.model.StatusSolicitacao;
import com.inventi.inventidemo.model.Urgencia;
import com.inventi.inventidemo.model.Usuario;
import com.inventi.inventidemo.repository.CategoriaRepository;
import com.inventi.inventidemo.repository.SetorRepository;
import com.inventi.inventidemo.repository.SolicitacaoRepository;
import com.inventi.inventidemo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

/** Portal do solicitante: o AuthInterceptor só deixa o perfil USUARIO chegar aqui. */
@Controller
public class SolicitacaoController {

    private static final int LIMITE_JUSTIFICATIVA = 500;

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SetorRepository setorRepository;
    private final CategoriaRepository categoriaRepository;

    public SolicitacaoController(SolicitacaoRepository solicitacaoRepository, UsuarioRepository usuarioRepository,
            SetorRepository setorRepository, CategoriaRepository categoriaRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.setorRepository = setorRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/solicitacao")
    public String exibir(HttpSession sessao, Model model) {
        Usuario usuario = usuarioDaSessao(sessao);
        if (usuario == null) {
            return "redirect:/logout";
        }
        model.addAttribute("solicitacoes",
                solicitacaoRepository.findBySolicitanteIdOrderByDataSolicitacaoDesc(usuario.getId()));
        model.addAttribute("categorias", categoriaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("setores", setorRepository.findAllByOrderByNomeAsc());
        model.addAttribute("urgencias", Urgencia.values());
        model.addAttribute("setorPadraoId", usuario.getSetor() == null ? null : usuario.getSetor().getId());
        return "solicitacao";
    }

    @PostMapping("/solicitacao")
    public String enviar(@RequestParam Long categoriaId, @RequestParam Long setorId,
            @RequestParam Urgencia urgencia, @RequestParam String justificativa,
            HttpSession sessao, RedirectAttributes redirect) {

        Usuario usuario = usuarioDaSessao(sessao);
        if (usuario == null) {
            return "redirect:/logout";
        }
        String texto = justificativa.trim();
        if (texto.isEmpty()) {
            redirect.addFlashAttribute("erro", "Descreva a justificativa da solicitação.");
            return "redirect:/solicitacao";
        }
        if (texto.length() > LIMITE_JUSTIFICATIVA) {
            redirect.addFlashAttribute("erro", "A justificativa pode ter no máximo " + LIMITE_JUSTIFICATIVA + " caracteres.");
            return "redirect:/solicitacao";
        }
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
        Setor setor = setorRepository.findById(setorId).orElse(null);
        if (categoria == null || setor == null) {
            redirect.addFlashAttribute("erro", "Selecione um tipo de equipamento e um setor válidos.");
            return "redirect:/solicitacao";
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setSolicitante(usuario);
        solicitacao.setCategoria(categoria);
        solicitacao.setSetor(setor);
        solicitacao.setUrgencia(urgencia);
        solicitacao.setJustificativa(texto);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacaoRepository.save(solicitacao);

        redirect.addFlashAttribute("mensagem", "Solicitação enviada. Acompanhe o andamento na tabela abaixo.");
        return "redirect:/solicitacao";
    }

    // Recarrega o usuário do banco (e ignora quem foi inativado depois do login)
    private Usuario usuarioDaSessao(HttpSession sessao) {
        if (sessao.getAttribute(LoginController.SESSAO) instanceof UsuarioSessao logado) {
            return usuarioRepository.findById(logado.getId()).filter(Usuario::isAtivo).orElse(null);
        }
        return null;
    }
}
