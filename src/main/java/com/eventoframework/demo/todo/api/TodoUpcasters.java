package com.eventoframework.demo.todo.api;

import com.evento.common.versioning.Upcaster;
import com.eventoframework.demo.todo.api.todo.event.TodoListCreatedEvent;

/**
 * The history of this context's payloads (Evento 3.0 payload versioning): every step turns an
 * older shape into the next one when it is read. The event store is never rewritten, and every
 * handler only sees the current shape.
 */
public final class TodoUpcasters {

    public static final Upcaster[] ALL = {
            // Revision 0 of TodoListCreatedEvent called the list name "content"
            Upcaster.from(TodoListCreatedEvent.class, 0).to(1, json -> json.set("name", json.remove("content"))),
    };

    private TodoUpcasters() {
    }
}
