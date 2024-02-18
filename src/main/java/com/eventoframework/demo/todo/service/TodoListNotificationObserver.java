package com.eventoframework.demo.todo.service;

import com.evento.common.messaging.gateway.CommandGateway;
import com.evento.common.messaging.gateway.QueryGateway;
import com.evento.common.modeling.annotations.component.Observer;
import com.evento.common.modeling.annotations.handler.EventHandler;
import com.evento.common.modeling.messaging.message.application.EventMessage;
import com.eventoframework.demo.todo.api.notification.NotificationSendCommand;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoCheckedEvent;
import com.eventoframework.demo.todo.api.todo.query.TodoListViewFindByIdentifierQuery;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

@Observer(version = 1)
public class TodoListNotificationObserver {

    @EventHandler
    public void on(TodoListTodoCheckedEvent event,
                   CommandGateway commandGateway,
                   QueryGateway queryGateway,
                   EventMessage<?> message) throws ExecutionException, InterruptedException {
        var todoList = queryGateway.query(new TodoListViewFindByIdentifierQuery(event.getIdentifier())).get().getData();
        var todo = todoList.getTodos().stream().filter(t -> t.getIdentifier().equals(event.getTodoIdentifier())).findFirst().orElseThrow();
        var body = """
                You have checked the todo "%s" (%s) inside %s (%s) at %s
                """.formatted(todo.getContent(), todo.getIdentifier(), todoList.getName(), todoList.getIdentifier(),
                DateTimeFormatter
                        .ofPattern("yyyy-MM-dd'T'hh:mm'Z'")
                        .withZone(ZoneOffset.UTC).format(Instant.ofEpochMilli(message.getTimestamp())));
        commandGateway.send(new NotificationSendCommand(body));
    }
}
