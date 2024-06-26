package com.eventoframework.demo.todo.api.todo.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainEvent;

@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListTodoCheckedEvent extends DomainEvent {

    private String identifier;
    private String todoIdentifier;
    // Communicate if all Todos inside this TodoList are checked with this check
    private boolean allChecked;
}
