package com.filmesync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endereço do WebSocket
        registry.addEndpoint("/ws-sync").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Falar sobre os destinos de mensagens que vamos usar
        registry.setApplicationDestinationPrefixes("/app");  // Prefixo para os destinos da aplicação
        registry.enableSimpleBroker("/sync");  // Habilita o broker simples para a transmissão dos comandos
    }
}
