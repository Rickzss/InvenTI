package com.inventi.inventidemo.controller;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventi.inventidemo.model.Solicitacao;
import com.inventi.inventidemo.model.StatusSolicitacao;
import com.inventi.inventidemo.repository.SolicitacaoRepository;

/**
 * Gestão das solicitações pelo Administrador. O AuthInterceptor só deixa o perfil
 * ADMINISTRADOR chegar aqui (o solicitante é redirecionado para /solicitacao).
 */
@Controller
public class SolicitacaoAdminController {

    private final SolicitacaoRepository solicitacaoRepository;

    public SolicitacaoAdminController(SolicitacaoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @GetMapping("/solicitacoes")
    public String listar(@RequestParam(value = "status", required = false) String status, Model model) {
        StatusSolicitacao filtro = converter(status);

        List<Solicitacao> solicitacoes = (filtro == null)
                ? solicitacaoRepository.findAllByOrderByDataSolicitacaoDesc()
                : solicitacaoRepository.findByStatusOrderByDataSolicitacaoDesc(filtro);

        Map<StatusSolicitacao, Long> contagens = new EnumMap<>(StatusSolicitacao.class);
        long total = 0;
        for (StatusSolicitacao s : StatusSolicitacao.values()) {
            long quantidade = solicitacaoRepository.countByStatus(s);
            contagens.put(s, quantidade);
            total += quantidade;
        }

        model.addAttribute("solicitacoes", solicitacoes);
        model.addAttribute("contagens", contagens);
        model.addAttribute("total", total);
        model.addAttribute("pendentes", contagens.get(StatusSolicitacao.PENDENTE));
        model.addAttribute("emAtendimento", contagens.get(StatusSolicitacao.EM_ATENDIMENTO));
        model.addAttribute("concluidas", contagens.get(StatusSolicitacao.CONCLUIDO));
        model.addAttribute("filtro", filtro);
        model.addAttribute("statusOpcoes", StatusSolicitacao.values());
        return "solicitacoes";
    }

    @PostMapping("/solicitacoes/{id}/status")
    public String atualizarStatus(@PathVariable Long id, @RequestParam StatusSolicitacao status,
            @RequestParam(value = "filtro", required = false) String filtro, RedirectAttributes redirect) {

        Solicitacao solicitacao = solicitacaoRepository.findById(id).orElse(null);
        if (solicitacao == null) {
            redirect.addFlashAttribute("erro", "Solicitação não encontrada.");
        } else {
            solicitacao.setStatus(status);
            solicitacaoRepository.save(solicitacao);
            redirect.addFlashAttribute("mensagem",
                    "Solicitação #" + id + " atualizada para \"" + status.getRotulo() + "\".");
        }

        // Mantém o filtro de status que o administrador estava usando
        StatusSolicitacao filtroAtual = converter(filtro);
        if (filtroAtual != null) {
            redirect.addAttribute("status", filtroAtual.name());
        }
        return "redirect:/solicitacoes";
    }

    // Parâmetro inválido/vazio vira "sem filtro" em vez de erro 400
    private StatusSolicitacao converter(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return StatusSolicitacao.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
