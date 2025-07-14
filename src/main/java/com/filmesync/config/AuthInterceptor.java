package com.filmesync.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SessaoUsuario sessaoUsuario;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Páginas públicas que não requerem autenticação
        String path = request.getRequestURI();

        // Permitir acesso a recursos estáticos e páginas públicas TESTE para Branch dos testes
        if (path.startsWith("/login") || path.startsWith("/registro") ||
            path.startsWith("/css") || path.startsWith("/js") ||
            path.startsWith("/images") || path.equals("/") ||
            path.equals("/logout") || path.startsWith("/debug")) {
            return true;
        }

        // Verifica se o usuário está logado
        if (!sessaoUsuario.isLogado()) {
            response.sendRedirect("/login");
            return false;
        }

        // Se o usuário for admin, permite acesso a tudo
        if (sessaoUsuario.isAdmin()) {
            return true;
        }

        // Verifica permissões para usuários não-admin
        if (path.startsWith("/selecionar-filme") && request.getMethod().equals("POST")) {
            response.sendRedirect("/acesso-negado");
            return false;
        }

        // Permite acesso a outras páginas para usuários logados
        return true;
    }
}
