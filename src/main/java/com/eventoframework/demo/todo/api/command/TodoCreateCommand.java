package com.eventoframework.demo.todo.api.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.evento.common.documentation.Domain;
import org.evento.common.modeling.messaging.payload.DomainCommand;

@Domain(name = "Todo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoCreateCommand extends DomainCommand {

    private String identifier;
    private String content;
    @Override
    public String getAggregateId() {
        return identifier;
    }
}
