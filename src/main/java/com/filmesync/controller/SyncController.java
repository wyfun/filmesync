package com.filmesync.controller;

import com.filmesync.config.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class SyncController {

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    // Método para controlar a sincronização do vídeo entre os clientes
    @MessageMapping("/controlar")
    @SendTo("/sync/acoes")
    public Map<String, Object> controlar(Map<String, Object> comando) {
        return comando;  // retransmite exatamente o que recebeu
    }

    // Endpoint para obter o número atual de utilizadores
    @GetMapping("/total-utilizadores")
    @ResponseBody
    public Map<String, Integer> getTotalUtilizadores() {
        return Map.of("total", webSocketEventListener.getTotalUtilizadores());
    }
}
