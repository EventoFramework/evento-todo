package com.eventoframework.demo.todo.service.invoker;

import com.eventoframework.demo.todo.api.todo.command.*;
import com.eventoframework.demo.todo.api.todo.query.TodoListListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.todo.query.TodoListViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.todo.view.TodoListView;
import com.evento.application.proxy.InvokerWrapper;
import com.evento.common.modeling.annotations.component.Invoker;
import com.evento.common.modeling.annotations.handler.InvocationHandler;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.modeling.messaging.query.Multiple;
import com.evento.common.modeling.messaging.query.Single;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Invoker
public class TodoInvoker extends InvokerWrapper {

    @SneakyThrows
    @InvocationHandler
    public String createTodoList(String name, String user){
        var identifier = "TDLS_" + UUID.randomUUID();
        getCommandGateway().sendAndWait(new TodoListCreateCommand(identifier, name), toUserMetadata(user));
        Thread.sleep(1000);
        return identifier;
    }

    @SneakyThrows
    @InvocationHandler
    public void deleteTodoList(String identifier, String user){
        getCommandGateway().sendAndWait(new TodoListDeleteCommand(identifier), toUserMetadata(user));
        Thread.sleep(1000);
    }

    @SneakyThrows
    @InvocationHandler
    public String addTodo(String identifier, String content, String user){
        var todoIdentifier = "TODO_" + UUID.randomUUID();
        getCommandGateway().sendAndWait(new TodoListAddTodoCommand(identifier, todoIdentifier, content), toUserMetadata(user));
        Thread.sleep(1000);
        return todoIdentifier;
    }

    @SneakyThrows
    @InvocationHandler
    public void checkTodo(String identifier, String todoIdentifier, String user){
        getCommandGateway().sendAndWait(new TodoListCheckTodoCommand(identifier, todoIdentifier), toUserMetadata(user));
        Thread.sleep(1000);
    }

    @SneakyThrows
    @InvocationHandler
    public void removeTodo(String identifier, String todoIdentifier, String user){
        getCommandGateway().sendAndWait(new TodoListRemoveTodoCommand(identifier, todoIdentifier), toUserMetadata(user));
        Thread.sleep(1000);
    }

    @InvocationHandler
    public CompletableFuture<TodoListView> findTodoListByIdentifier(String identifier){
        return getQueryGateway().query(new TodoListViewFindByIdentifierQuery(identifier)).thenApply(Single::getData);
    }

    @InvocationHandler
    public CompletableFuture<Collection<TodoListListItemView>> searchTodoList(String query, int page){
        return getQueryGateway().query(new TodoListListItemViewSearchQuery(query, page, 15))
                .thenApply(Multiple::getData);
    }

    private Metadata toUserMetadata(String user) {
        var m = new Metadata();
        m.put("user", user);
        return m;
    }
}
