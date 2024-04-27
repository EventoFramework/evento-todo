package com.eventoframework.demo.todo.api.todo.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainCommand;

@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListAddTodoCommand implements DomainCommand {

    // Identifier of the TodoList to update
    private String identifier;
    // Identifier of the To-do to delete
    private String todoIdentifier;
    // The To-do content
    private String content;
    @Override
    public String getAggregateId() {
        return identifier;
    }
}
