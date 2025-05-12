package com.filmesync.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class SyncController {

    @MessageMapping("/controlar")
    @SendTo("/sync/acoes")
    public Map<String, Object> controlar(Map<String, Object> comando) {
        return comando; // retransmite exatamente o que recebeu
    }
}
