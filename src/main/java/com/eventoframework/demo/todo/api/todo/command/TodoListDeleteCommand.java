package com.eventoframework.demo.todo.api.todo.command;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListDeleteCommand  implements DomainCommand {

    // Identifier of the TodoList to delete
    private String identifier;

    @Override
    public String getAggregateId() {
        return identifier;
    }
}