package com.inventi.inventidemo.controller;

import com.inventi.inventidemo.model.Cliente;
import com.inventi.inventidemo.repository.ClienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Listar clientes e exibir o formulário vazio
    @GetMapping("/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("cliente", new Cliente());
        return "clientes";
    }

    // Salvar: insere se o id for nulo, atualiza se já existir
    @PostMapping("/clientes")
    public String salvarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }

    // Alterar: recarrega a página com o formulário preenchido
    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            return "redirect:/clientes"; // id inexistente
        }
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("cliente", cliente);
        return "clientes";
    }

    // Excluir
    @PostMapping("/clientes/{id}/excluir")
    public String excluirCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return "redirect:/clientes";
    }
}