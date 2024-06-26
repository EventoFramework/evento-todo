package com.eventoframework.demo.todo.query;

import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.utils.ProjectorStatus;
import com.eventoframework.demo.todo.api.erp.event.ErpUserActivityRegisteredEvent;
import com.eventoframework.demo.todo.api.todo.event.*;
import com.eventoframework.demo.todo.query.model.Todo;
import com.eventoframework.demo.todo.query.model.TodoList;
import com.eventoframework.demo.todo.query.model.TodoListRepository;
import com.evento.common.modeling.annotations.component.Projector;
import com.evento.common.modeling.annotations.handler.EventHandler;
import com.evento.common.modeling.messaging.message.application.EventMessage;
import com.eventoframework.demo.todo.query.model.TodoListStatus;
import com.eventoframework.demo.todo.utils.RealtimeUpdateManager;
import com.eventoframework.demo.todo.utils.RealtimeUpdatesService;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

@Projector(version = 1)
public class TodoListProjector extends RealtimeUpdateManager<TodoList, String> {


    public TodoListProjector(TodoListRepository repository, RealtimeUpdatesService realtimeUpdatesService) {
        super(repository, realtimeUpdatesService);
    }

    @EventHandler
    public void on(TodoListCreatedEvent event, EventMessage<TodoListCreatedEvent> message,
                   ProjectorStatus projectorStatus) {
        save(new TodoList(
                event.getIdentifier(),
                event.getContent(),
                message.getMetadata().get("user"),
                null,
                Instant.ofEpochMilli(message.getTimestamp()).atZone(ZoneId.systemDefault()),
                null,
                new ArrayList<>(),
                null,
                TodoListStatus.WIP
        ), projectorStatus);
    }

    @EventHandler
    public void on(TodoListDeletedEvent event, EventMessage<TodoListCreatedEvent> message,
                   ProjectorStatus projectorStatus) {
        delete(repository.findById(event.getIdentifier()).orElseThrow(), projectorStatus);
    }

    @EventHandler
    public void on(TodoListTodoAddedEvent event, Metadata metadata, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        var td = new Todo(
                event.getTodoIdentifier(),
                event.getContent(),
                metadata.get("user"),
                null,
                timestamp.atZone(ZoneId.systemDefault()),
                null
        );
        list.getTodos().add(td);
        list.setUpdatedAt(td.getCreatedAt());
        list.setUpdatedBy(td.getCreatedBy());
        update(list, projectorStatus);
    }

    @EventHandler
    public void on(TodoListTodoRemovedEvent event, Metadata metadata, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        list.getTodos().removeIf(t -> event.getTodoIdentifier().equals(t.getIdentifier()));
        list.setUpdatedAt(timestamp.atZone(ZoneId.systemDefault()));
        list.setUpdatedBy(metadata.get("user"));
        update(list, projectorStatus);
    }

    @EventHandler
    public void on(TodoListTodoCheckedEvent event, Metadata metadata, Instant timestamp,
                   ProjectorStatus projectorStatus) {
        var list = repository.findById(event.getIdentifier()).orElseThrow();
        var td = list.getTodos().stream().filter(t -> event.getTodoIdentifier().equals(t.getIdentifier())).findFirst().orElseThrow();
        td.setCompletedAt(timestamp.atZone(ZoneId.systemDefault()));
        td.setCompletedBy(metadata.get("user"));
        list.setUpdatedAt(td.getCompletedAt());
        list.setUpdatedBy(td.getCompletedBy());
        if(event.isAllChecked()){
            list.setStatus(TodoListStatus.REGISTRATION_PENDING);
        }
        update(list, projectorStatus);
    }

    @EventHandler
    public void on(ErpUserActivityRegisteredEvent event,
                   Instant timestamp,
                   ProjectorStatus projectorStatus){
        if("TodoList".equals(event.getResourceType())){
            var list = repository.findById(event.getResourceIdentifier()).orElseThrow();
            list.setRegisteredAt(timestamp.atZone(ZoneId.systemDefault()));
            list.setStatus(TodoListStatus.REGISTERED);
            update(list, projectorStatus);
        }
    }
}
