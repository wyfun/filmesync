package com.filmesync.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Set<String> sessions = ConcurrentHashMap.newKeySet();

    // Evento chamado quando um cliente se conecta
    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        sessions.add(sessionId);
        enviarTotal();
    }

    // Evento chamado quando um cliente se desconecta
    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        sessions.remove(event.getSessionId());
        enviarTotal();
    }

    // Evento chamado quando um cliente se subscreve a um tópico
    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        String destination = event.getMessage().getHeaders().get("simpDestination").toString();
        // Se o cliente se subscrever ao tópico de utilizadores, enviar o total atual
        if ("/sync/utilizadores".equals(destination)) {
            enviarTotal();
        }
    }

    // Envia o número de utilizadores para todos os clientes
    private void enviarTotal() {
        messagingTemplate.convertAndSend("/sync/utilizadores", Map.of("total", sessions.size()));
    }

    // Método para obter o número atual de utilizadores
    public int getTotalUtilizadores() {
        return sessions.size();
    }
}
