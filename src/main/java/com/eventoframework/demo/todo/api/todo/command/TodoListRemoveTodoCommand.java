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
public class TodoListRemoveTodoCommand implements DomainCommand {

    // Identifier of the TodoList to update
    private String identifier;
    // Identifier of the To-do to remove
    private String todoIdentifier;
    @Override
    public String getAggregateId() {
        return identifier;
    }
}
