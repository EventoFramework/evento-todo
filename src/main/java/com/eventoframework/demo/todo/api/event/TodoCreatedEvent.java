package com.eventoframework.demo.todo.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.evento.common.documentation.Domain;
import org.evento.common.modeling.messaging.payload.DomainCommand;
import org.evento.common.modeling.messaging.payload.DomainEvent;

@Domain(name = "Todo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoCreatedEvent extends DomainEvent {

    private String identifier;
    private String content;
}
