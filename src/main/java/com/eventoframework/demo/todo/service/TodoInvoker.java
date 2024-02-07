package com.eventoframework.demo.todo.service;

import com.eventoframework.demo.todo.api.command.*;
import com.eventoframework.demo.todo.api.query.TodoListListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.query.TodoListViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.view.TodoListView;
import com.evento.application.proxy.InvokerWrapper;
import com.evento.common.modeling.annotations.component.Invoker;
import com.evento.common.modeling.annotations.handler.InvocationHandler;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.modeling.messaging.query.Multiple;
import com.evento.common.modeling.messaging.query.Single;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Invoker
public class TodoInvoker extends InvokerWrapper {

    @InvocationHandler
    public String createTodoList(String name, String user){
        var identifier = "TDLS_" + UUID.randomUUID();
        getCommandGateway().sendAndWait(new TodoListCreateCommand(identifier, name), toUserMetadata(user));
        return identifier;
    }

    @InvocationHandler
    public void deleteTodoList(String identifier, String user){
        getCommandGateway().sendAndWait(new TodoListDeleteCommand(identifier), toUserMetadata(user));
    }

    @InvocationHandler
    public String addTodo(String identifier, String content, String user){
        var todoIdentifier = "TODO_" + UUID.randomUUID();
        getCommandGateway().sendAndWait(new TodoListAddTodoCommand(identifier, todoIdentifier, content), toUserMetadata(user));
        return todoIdentifier;
    }

    @InvocationHandler
    public void checkTodo(String identifier, String todoIdentifier, String user){
        getCommandGateway().sendAndWait(new TodoListCheckTodoCommand(identifier, todoIdentifier), toUserMetadata(user));
    }

    @InvocationHandler
    public void removeTodo(String identifier, String todoIdentifier, String user){
        getCommandGateway().sendAndWait(new TodoListRemoveTodoCommand(identifier, todoIdentifier), toUserMetadata(user));
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
