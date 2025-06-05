package com.filmesync.config;

import com.filmesync.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class SessaoUsuario {
    
    private Usuario usuarioLogado;
    
    public boolean isLogado() {
        return usuarioLogado != null;
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }
    
    public void logout() {
        this.usuarioLogado = null;
    }
    
    public boolean isAdmin() {
        return isLogado() && usuarioLogado.isAdmin();
    }
    
    public String getUsername() {
        return isLogado() ? usuarioLogado.getUsername() : null;
    }
}
