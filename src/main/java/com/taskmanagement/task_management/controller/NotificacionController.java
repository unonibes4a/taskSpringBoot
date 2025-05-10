package com.taskmanagement.task_management.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificacionController {
 
@MessageMapping("/sendMessage")  
  @SendTo("/topic/asignaciones")
  public String saludar(String mensaje) {
    System.out.println("Mensaje recibido desde frontend: " + mensaje);
    return "Respuesta del backend: " + mensaje;
  }

}
