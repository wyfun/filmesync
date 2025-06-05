package com.filmesync.service;

import com.filmesync.model.Usuario;
import com.filmesync.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Inicializa o usuário admin padrão se não existir
     */
    @PostConstruct
    public void init() {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario("admin", "admin", true);
            usuarioRepository.save(admin);
            System.out.println("Usuário admin criado com sucesso!");
        }
    }

    /**
     * Autentica um usuário
     */
    public Usuario autenticar(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario.get();
        }

        return null;
    }

    /**
     * Registra um novo usuário (não-admin por padrão)
     */
    public Usuario registrar(String username, String password) {
        if (usuarioRepository.existsByUsername(username)) {
            return null; // Usuário já existe
        }

        Usuario novoUsuario = new Usuario(username, password, false);
        return usuarioRepository.save(novoUsuario);
    }

    /**
     * Verifica se um usuário é administrador
     */
    public boolean isAdmin(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.isPresent() && usuario.get().isAdmin();
    }


}
