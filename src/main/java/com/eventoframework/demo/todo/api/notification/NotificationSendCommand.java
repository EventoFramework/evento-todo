package com.eventoframework.demo.todo.api.notification;

import com.evento.common.modeling.messaging.payload.ServiceCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSendCommand implements ServiceCommand {
    private String body;
}
