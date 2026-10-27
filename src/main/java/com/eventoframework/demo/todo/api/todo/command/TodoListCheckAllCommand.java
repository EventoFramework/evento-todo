package com.eventoframework.demo.todo.api.todo.command;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Check every open todo of a list at once: one command, one event per todo. */
@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListCheckAllCommand extends DomainCommand {

    private String identifier;

    @Override
    public String getAggregateId() {
        return identifier;
    }
}
