package com.taskmanagement.task_management.controller;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class WebSocketEventController {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Nueva conexión: " + event.getUser());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("Conexión cerrada: " + event.getSessionId());
    }
}