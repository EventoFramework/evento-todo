package com.eventoframework.demo.todo.query;

import com.eventoframework.demo.todo.api.todo.event.*;
import com.eventoframework.demo.todo.query.model.Todo;
import com.eventoframework.demo.todo.query.model.TodoList;
import com.eventoframework.demo.todo.query.model.TodoListRepository;
import com.evento.common.modeling.annotations.component.Projector;
import com.evento.common.modeling.annotations.handler.EventHandler;
import com.evento.common.modeling.messaging.message.application.EventMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

@Projector(version = 1)
public class TodoProjector {

    private final TodoListRepository repository;

    public TodoProjector(TodoListRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(TodoListCreatedEvent event, EventMessage<TodoListCreatedEvent> message) {
        repository.save(new TodoList(
                event.getIdentifier(),
                event.getContent(),
                message.getMetadata().get("user"),
                null,
                Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.systemDefault()),
                null,
                new ArrayList<>()
        ));
    }

    @EventHandler
    public void on(TodoListDeletedEvent event, EventMessage<TodoListCreatedEvent> message) {
        repository.deleteById(event.getIdentifier());
    }

    @EventHandler
    public void on(TodoListTodoAddedEvent event, EventMessage<TodoListCreatedEvent> message) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        var td = new Todo(
                event.getTodoIdentifier(),
                event.getContent(),
                message.getMetadata().get("user"),
                null,
                ZonedDateTime.now(),
                null
        );
        list.getTodos().add(td);
        list.setUpdatedAt(td.getCreatedAt());
        list.setUpdatedBy(td.getCreatedBy());
        repository.save(list);
    }

    @EventHandler
    public void on(TodoListTodoRemovedEvent event, EventMessage<TodoListCreatedEvent> message) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        list.getTodos().removeIf(t -> event.getTodoIdentifier().equals(t.getIdentifier()));
        list.setUpdatedAt(ZonedDateTime.now());
        list.setUpdatedBy(message.getMetadata().get("user"));
        repository.save(list);
    }

    @EventHandler
    public void on(TodoListTodoCheckedEvent event, EventMessage<TodoListCreatedEvent> message) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        var td = list.getTodos().stream().filter(t -> event.getTodoIdentifier().equals(t.getIdentifier())).findFirst().orElseThrow();
        td.setCompletedAt(ZonedDateTime.now());
        td.setCompletedBy(message.getMetadata().get("user"));
        list.setUpdatedAt(td.getCompletedAt());
        list.setUpdatedBy(td.getCompletedBy());
        repository.save(list);
    }
}
