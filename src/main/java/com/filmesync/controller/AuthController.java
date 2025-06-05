package com.filmesync.controller;

import com.filmesync.config.SessaoUsuario;
import com.filmesync.model.Usuario;
import com.filmesync.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SessaoUsuario sessaoUsuario;

    @GetMapping("/login")
    public String paginaLogin() {
        // Se já estiver logado, redireciona para a página inicial
        if (sessaoUsuario.isLogado()) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String fazerLogin(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {

        Usuario usuario = usuarioService.autenticar(username, password);

        if (usuario != null) {
            sessaoUsuario.setUsuarioLogado(usuario);
            return "redirect:/";
        } else {
            model.addAttribute("erro", "Usuário ou senha inválidos");
            return "login";
        }
    }

    @GetMapping("/registro")
    public String paginaRegistro() {
        // Se já estiver logado, redireciona para a página inicial
        if (sessaoUsuario.isLogado()) {
            return "redirect:/";
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 Model model) {

        // Verifica se as senhas coincidem
        if (!password.equals(confirmPassword)) {
            model.addAttribute("erro", "As senhas não coincidem");
            return "registro";
        }

        // Tenta registrar o usuário
        Usuario novoUsuario = usuarioService.registrar(username, password);

        if (novoUsuario != null) {
            // Faz login automático após o registro
            sessaoUsuario.setUsuarioLogado(novoUsuario);
            return "redirect:/";
        } else {
            model.addAttribute("erro", "Nome de usuário já existe");
            return "registro";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        sessaoUsuario.logout();
        return "redirect:/login?logout=true";
    }


}
