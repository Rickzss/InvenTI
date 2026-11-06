package com.inventi.inventidemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EquipamentoController {

    // Por enquanto só abre o template. Depois entram o repository e o Model.
    @GetMapping("/equipamentos")
    public String listarEquipamentos() {
        return "equipamentos";
    }
}