package com.eventoframework.demo.todo.api.todo.event;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainEvent;
import com.evento.common.versioning.Revision;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Revision 1: the list name was called {@code content} in revision 0. Events stored with the old
 * shape are upcast when read (see {@code TodoUpcasters}).
 */
@Revision(1)
@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListCreatedEvent extends DomainEvent {

    private String identifier;
    private String name;
}
