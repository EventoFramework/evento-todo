package com.eventoframework.demo.todo.query;

import com.eventoframework.demo.todo.api.event.TodoCheckedSetEvent;
import com.eventoframework.demo.todo.api.event.TodoCreatedEvent;
import com.eventoframework.demo.todo.api.event.TodoDeletedEvent;
import com.eventoframework.demo.todo.query.model.Todo;
import com.eventoframework.demo.todo.query.model.TodoRepository;
import org.evento.common.modeling.annotations.component.Projector;
import org.evento.common.modeling.annotations.handler.EventHandler;
import org.evento.common.modeling.messaging.message.application.EventMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Projector(version = 1)
public class TodoProjector {

    private final TodoRepository repository;

    public TodoProjector(TodoRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(TodoCreatedEvent event, EventMessage<TodoCreatedEvent> message){
        repository.save(new Todo(
                event.getIdentifier(),
                event.getContent(),
                false,
                message.getMetadata().get("user"),
                null,
                Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.systemDefault()),
                null
        ));
    }

    @EventHandler
    public void on(TodoCheckedSetEvent event){
       var todo =  repository.findById(event.getIdentifier()).orElseThrow();
       todo.setCompleted(event.isChecked());
       repository.save(todo);
    }

    @EventHandler
    public void on(TodoDeletedEvent event){
        var todo =  repository.findById(event.getIdentifier()).orElseThrow();
        repository.delete(todo);
    }
}
