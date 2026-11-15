package com.inventi.inventidemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.inventi.inventidemo.model.Categoria;
import com.inventi.inventidemo.model.Equipamento;
import com.inventi.inventidemo.model.Perfil;
import com.inventi.inventidemo.model.Setor;
import com.inventi.inventidemo.model.StatusEquipamento;
import com.inventi.inventidemo.model.Usuario;
import com.inventi.inventidemo.repository.CategoriaRepository;
import com.inventi.inventidemo.repository.EquipamentoRepository;
import com.inventi.inventidemo.repository.SetorRepository;
import com.inventi.inventidemo.repository.UsuarioRepository;
import com.inventi.inventidemo.util.SenhaUtil;

@Controller
public class CadastroController {

    private static final int SENHA_MINIMA = 6;

    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SetorRepository setorRepository;
    private final CategoriaRepository categoriaRepository;

    public CadastroController(EquipamentoRepository equipamentoRepository, UsuarioRepository usuarioRepository,
            SetorRepository setorRepository, CategoriaRepository categoriaRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.setorRepository = setorRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // Tela com duas abas: equipamento (padrão) e usuário do sistema
    @GetMapping("/cadastro")
    public String exibir(@RequestParam(defaultValue = "equipamento") String aba, Model model) {
        model.addAttribute("aba", "usuario".equals(aba) ? "usuario" : "equipamento");
        model.addAttribute("categorias", categoriaRepository.findAllByOrderByNomeAsc());
        model.addAttribute("setores", setorRepository.findAllByOrderByNomeAsc());
        model.addAttribute("statusList", StatusEquipamento.values());
        model.addAttribute("perfis", Perfil.values());
        return "cadastro";
    }

    @PostMapping("/cadastro/equipamento")
    public String salvarEquipamento(@RequestParam String nome, @RequestParam String patrimonio,
            @RequestParam Long categoriaId, @RequestParam Long setorId,
            @RequestParam(required = false) String marca, @RequestParam(required = false) String modelo,
            @RequestParam StatusEquipamento status, RedirectAttributes redirect) {

        String destino = "redirect:/cadastro?aba=equipamento";

        if (nome.isBlank() || patrimonio.isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o nome e o código do equipamento.");
            return destino;
        }
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
        Setor setor = setorRepository.findById(setorId).orElse(null);
        if (categoria == null || setor == null) {
            redirect.addFlashAttribute("erro", "Selecione um tipo e um setor válidos.");
            return destino;
        }
        if (equipamentoRepository.existsByPatrimonioIgnoreCase(patrimonio.trim())) {
            redirect.addFlashAttribute("erro", "Já existe um equipamento com o código " + patrimonio.trim() + ".");
            return destino;
        }

        Equipamento equipamento = new Equipamento();
        equipamento.setNome(nome.trim());
        equipamento.setPatrimonio(patrimonio.trim().toUpperCase());
        equipamento.setCategoria(categoria);
        equipamento.setSetor(setor);
        equipamento.setMarca(limpar(marca));
        equipamento.setModelo(limpar(modelo));
        equipamento.setStatus(status);
        equipamentoRepository.save(equipamento);

        redirect.addFlashAttribute("mensagem", "Equipamento " + equipamento.getPatrimonio() + " cadastrado.");
        return destino;
    }

    @PostMapping("/cadastro/usuario")
    public String salvarUsuario(@RequestParam String nome, @RequestParam String email,
            @RequestParam String senha, @RequestParam(required = false) Long setorId,
            @RequestParam Perfil perfil, RedirectAttributes redirect) {

        String destino = "redirect:/cadastro?aba=usuario";

        if (nome.isBlank() || email.isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o nome e o e-mail do usuário.");
            return destino;
        }
        if (senha.length() < SENHA_MINIMA) {
            redirect.addFlashAttribute("erro", "A senha precisa ter pelo menos " + SENHA_MINIMA + " caracteres.");
            return destino;
        }
        if (usuarioRepository.existsByEmailIgnoreCase(email.trim())) {
            redirect.addFlashAttribute("erro", "O e-mail " + email.trim() + " já está cadastrado.");
            return destino;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim());
        usuario.setSenha(SenhaUtil.gerarHash(senha));
        usuario.setPerfil(perfil);
        usuario.setSetor(setorId == null ? null : setorRepository.findById(setorId).orElse(null));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        redirect.addFlashAttribute("mensagem", "Usuário " + usuario.getNome() + " cadastrado.");
        return destino;
    }

    private static String limpar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
