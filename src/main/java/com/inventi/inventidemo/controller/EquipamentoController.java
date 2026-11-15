package com.inventi.inventidemo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inventi.inventidemo.model.Equipamento;
import com.inventi.inventidemo.repository.EquipamentoRepository;

@Controller
public class EquipamentoController {

    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoController(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    @GetMapping("/equipamentos")
    public String listarEquipamentos(@RequestParam(value = "nome", required = false) String nome, Model model) {
        boolean filtrando = nome != null && !nome.isBlank();

        List<Equipamento> equipamentos = filtrando
                ? equipamentoRepository.findByNomeContainingIgnoreCaseOrderByDataCadastroDesc(nome.trim())
                : equipamentoRepository.findAllByOrderByDataCadastroDesc();

        model.addAttribute("equipamentos", equipamentos);
        model.addAttribute("nome", filtrando ? nome.trim() : "");
        model.addAttribute("filtrando", filtrando);
        return "equipamentos";
    }
}
