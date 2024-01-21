package com.eventoframework.demo.todo.service;

import com.eventoframework.demo.todo.api.command.TodoCreateCommand;
import com.eventoframework.demo.todo.api.command.TodoDeleteCommand;
import com.eventoframework.demo.todo.api.command.TodoSetCheckedCommand;
import com.eventoframework.demo.todo.api.query.TodoListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.query.TodoViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.view.TodoListItemView;
import com.eventoframework.demo.todo.api.view.TodoView;
import org.evento.application.proxy.InvokerWrapper;
import org.evento.common.modeling.annotations.component.Invoker;
import org.evento.common.modeling.annotations.handler.InvocationHandler;
import org.evento.common.modeling.messaging.message.application.Metadata;
import org.evento.common.modeling.messaging.query.Multiple;
import org.evento.common.modeling.messaging.query.Single;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Invoker
public class TodoInvoker extends InvokerWrapper {

    @InvocationHandler
    public void createTodo(String identifier, String content, String user){
        getCommandGateway().sendAndWait(new TodoCreateCommand(identifier, content), toUserMetadata(user));
    }

    @InvocationHandler
    public void checkTodo(String identifier, boolean checked, String user){
        getCommandGateway().sendAndWait(new TodoSetCheckedCommand(identifier, checked), toUserMetadata(user));
    }

    @InvocationHandler
    public void deleteTodo(String identifier, String user){
        getCommandGateway().sendAndWait(new TodoDeleteCommand(identifier), toUserMetadata(user));
    }

    @InvocationHandler
    public CompletableFuture<TodoView> findTodoByIdentifier(String identifier){
        return getQueryGateway().query(new TodoViewFindByIdentifierQuery(identifier)).thenApply(Single::getData);
    }

    @InvocationHandler
    public CompletableFuture<Collection<TodoListItemView>> searchTodo(String query, int page){
        return getQueryGateway().query(new TodoListItemViewSearchQuery(query, page, 15))
                .whenComplete((s,e) -> {
                    System.out.println("ciao");
                }).whenComplete((s,e) -> {
                    System.out.println("ciao2");
                }).thenApply(Multiple::getData)
                ;
    }

    private Metadata toUserMetadata(String user) {
        var m = new Metadata();
        m.put("user", user);
        return m;
    }
}
