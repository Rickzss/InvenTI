package com.inventi.inventidemo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inventi.inventidemo.dto.BarraGrafico;
import com.inventi.inventidemo.model.StatusEquipamento;
import com.inventi.inventidemo.repository.ContagemPorRotulo;
import com.inventi.inventidemo.repository.EquipamentoRepository;
import com.inventi.inventidemo.repository.UsuarioRepository;

@Controller
public class DashboardController {

    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardController(EquipamentoRepository equipamentoRepository, UsuarioRepository usuarioRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {
        long totalEquipamentos = equipamentoRepository.count();

        // Cards de resumo
        model.addAttribute("totalEquipamentos", totalEquipamentos);
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("emManutencao", equipamentoRepository.countByStatus(StatusEquipamento.EM_MANUTENCAO));
        model.addAttribute("inativos", equipamentoRepository.countByStatus(StatusEquipamento.INATIVO));

        // Gráficos de barras (largura proporcional ao total de equipamentos)
        model.addAttribute("porTipo", montarBarras(equipamentoRepository.contarPorCategoria(), totalEquipamentos));
        model.addAttribute("porSetor", montarBarras(equipamentoRepository.contarPorSetor(), totalEquipamentos));

        // Tabela de últimos cadastros
        model.addAttribute("ultimos", equipamentoRepository.findTop5ByOrderByDataCadastroDescIdDesc());
        return "dashboard";
    }

    private List<BarraGrafico> montarBarras(List<ContagemPorRotulo> contagens, long total) {
        return contagens.stream()
                .map(c -> new BarraGrafico(
                        c.getRotulo() == null ? "Sem setor" : c.getRotulo(),
                        c.getTotal(),
                        total == 0 ? 0 : (int) Math.round(c.getTotal() * 100.0 / total)))
                .toList();
    }
}
