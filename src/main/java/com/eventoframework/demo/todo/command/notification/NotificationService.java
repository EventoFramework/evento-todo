package com.eventoframework.demo.todo.command.notification;

import com.evento.common.modeling.annotations.component.Service;
import com.evento.common.modeling.annotations.handler.CommandHandler;
import com.evento.common.utils.Sleep;
import com.eventoframework.demo.todo.api.notification.NotificationSendCommand;

@Service
public class NotificationService {

    @CommandHandler
    public void handle(NotificationSendCommand command){
        Sleep.apply((1 + ((int)(Math.random() * 10))) * 1000 );
        System.out.println("Notification: " + command.getBody());
    }
}
