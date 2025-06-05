package com.filmesync.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class ThymeleafConfig implements WebMvcConfigurer {

    @Autowired
    private SessaoUsuario sessaoUsuario;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                  ModelAndView modelAndView) throws Exception {
                if (modelAndView != null) {
                    // Adiciona o usuário logado a todas as páginas
                    modelAndView.addObject("usuarioLogado", sessaoUsuario.getUsuarioLogado());
                    modelAndView.addObject("nomeUsuario", sessaoUsuario.getUsername());
                    modelAndView.addObject("isAdmin", sessaoUsuario.isAdmin());
                    modelAndView.addObject("isLogado", sessaoUsuario.isLogado());
                }
            }
        });
    }
}
