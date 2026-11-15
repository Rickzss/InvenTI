package com.inventi.inventidemo.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.inventi.inventidemo.model.Categoria;
import com.inventi.inventidemo.model.Perfil;
import com.inventi.inventidemo.model.Setor;
import com.inventi.inventidemo.model.Usuario;
import com.inventi.inventidemo.repository.CategoriaRepository;
import com.inventi.inventidemo.repository.SetorRepository;
import com.inventi.inventidemo.repository.UsuarioRepository;
import com.inventi.inventidemo.util.SenhaUtil;

/**
 * Carga inicial (só roda se as tabelas estiverem vazias): setores, tipos de
 * equipamento e dois usuários de teste. TROQUE as senhas fora do ambiente de demonstração.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final SetorRepository setorRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(SetorRepository setorRepository, CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository) {
        this.setorRepository = setorRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (setorRepository.count() == 0) {
            for (String nome : List.of("TI", "Financeiro", "RH", "Operações")) {
                setorRepository.save(new Setor(nome));
            }
        }
        if (categoriaRepository.count() == 0) {
            for (String nome : List.of("Notebook", "Impressora", "Switch", "Monitor", "Periférico")) {
                categoriaRepository.save(new Categoria(nome));
            }
        }
        if (usuarioRepository.count() == 0) {
            Setor ti = setorRepository.findByNome("TI").orElse(null);
            Setor rh = setorRepository.findByNome("RH").orElse(null);
            usuarioRepository.save(novoUsuario("Admin", "admin@inventi.com", "admin123", Perfil.ADMINISTRADOR, ti));
            usuarioRepository.save(novoUsuario("Ricardo Passos", "solicitante@inventi.com", "user123", Perfil.USUARIO, rh));
        }
    }

    private Usuario novoUsuario(String nome, String email, String senha, Perfil perfil, Setor setor) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(SenhaUtil.gerarHash(senha));
        usuario.setPerfil(perfil);
        usuario.setSetor(setor);
        usuario.setAtivo(true);
        return usuario;
    }
}
