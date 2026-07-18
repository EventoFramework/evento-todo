package com.eventoframework.demo.todo.query;

import com.evento.common.modeling.annotations.component.Projector;
import com.evento.common.modeling.annotations.handler.EventHandler;
import com.evento.common.modeling.messaging.message.application.EventMessage;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.utils.ProjectorStatus;
import com.eventoframework.demo.todo.api.erp.event.ErpUserActivityRegisteredEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListCreatedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListDeletedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoAddedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoCheckedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoRemovedEvent;
import com.eventoframework.demo.todo.query.model.Todo;
import com.eventoframework.demo.todo.query.model.TodoList;
import com.eventoframework.demo.todo.query.model.TodoListRepository;
import com.eventoframework.demo.todo.query.model.TodoListStatus;
import com.eventoframework.demo.todo.realtime.SseUpdatesService;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Consumes the TodoList event stream in order and materializes the JPA read
 * model. Once the consumer has reached the head of the stream, every change is
 * also pushed to open browser pages over SSE — during replay (rebuild of the
 * read model) no notifications are sent.
 */
@Projector(version = 2)
public class TodoListProjector {

    private final TodoListRepository repository;
    private final SseUpdatesService sse;

    public TodoListProjector(TodoListRepository repository, SseUpdatesService sse) {
        this.repository = repository;
        this.sse = sse;
    }

    @EventHandler
    public void on(TodoListCreatedEvent event, EventMessage<TodoListCreatedEvent> message,
                   ProjectorStatus projectorStatus) {
        repository.save(new TodoList(
                event.getIdentifier(),
                event.getContent(),
                message.getMetadata() == null ? null : message.getMetadata().get("user"),
                null,
                Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.systemDefault()),
                null,
                new ArrayList<>(),
                null,
                TodoListStatus.WIP
        ));
        notifyIfLive(event.getIdentifier(), projectorStatus);
    }

    @EventHandler
    public void on(TodoListDeletedEvent event, ProjectorStatus projectorStatus) {
        repository.deleteById(event.getIdentifier());
        notifyIfLive(event.getIdentifier(), projectorStatus);
    }

    @EventHandler
    public void on(TodoListTodoAddedEvent event, Metadata metadata, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        var todo = new Todo(
                event.getTodoIdentifier(),
                event.getContent(),
                metadata == null ? null : metadata.get("user"),
                null,
                timestamp.atZone(ZoneId.systemDefault()),
                null
        );
        list.getTodos().add(todo);
        list.setUpdatedAt(todo.getCreatedAt());
        list.setUpdatedBy(todo.getCreatedBy());
        repository.save(list);
        notifyIfLive(event.getIdentifier(), projectorStatus);
    }

    @EventHandler
    public void on(TodoListTodoRemovedEvent event, Metadata metadata, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        list.getTodos().removeIf(t -> event.getTodoIdentifier().equals(t.getIdentifier()));
        list.setUpdatedAt(timestamp.atZone(ZoneId.systemDefault()));
        list.setUpdatedBy(metadata == null ? null : metadata.get("user"));
        repository.save(list);
        notifyIfLive(event.getIdentifier(), projectorStatus);
    }

    @EventHandler
    public void on(TodoListTodoCheckedEvent event, Metadata metadata, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        var todo = list.getTodos().stream()
                .filter(t -> event.getTodoIdentifier().equals(t.getIdentifier()))
                .findFirst().orElseThrow();
        todo.setCompletedAt(timestamp.atZone(ZoneId.systemDefault()));
        todo.setCompletedBy(metadata == null ? null : metadata.get("user"));
        list.setUpdatedAt(todo.getCompletedAt());
        list.setUpdatedBy(todo.getCompletedBy());
        if (event.isAllChecked()) {
            list.setStatus(TodoListStatus.REGISTRATION_PENDING);
        }
        repository.save(list);
        notifyIfLive(event.getIdentifier(), projectorStatus);
    }

    @EventHandler
    public void on(ErpUserActivityRegisteredEvent event, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        if ("TodoList".equals(event.getResourceType())) {
            repository.findById(event.getResourceIdentifier()).ifPresent(list -> {
                list.setRegisteredAt(timestamp.atZone(ZoneId.systemDefault()));
                list.setStatus(TodoListStatus.REGISTERED);
                repository.save(list);
                notifyIfLive(event.getResourceIdentifier(), projectorStatus);
            });
        }
    }

    private void notifyIfLive(String listId, ProjectorStatus projectorStatus) {
        if (projectorStatus.isHeadReached()) {
            sse.notifyListChanged(listId);
        }
    }
}
