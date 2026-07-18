package com.eventoframework.demo.todo.service.invoker;

import com.evento.application.proxy.InvokerWrapper;
import com.evento.common.modeling.annotations.component.Invoker;
import com.evento.common.modeling.annotations.handler.InvocationHandler;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.modeling.messaging.query.Multiple;
import com.evento.common.modeling.messaging.query.Single;
import com.eventoframework.demo.todo.api.todo.command.TodoListAddTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCheckTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCreateCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListDeleteCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListRemoveTodoCommand;
import com.eventoframework.demo.todo.api.todo.query.TodoListListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.todo.query.TodoListViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.todo.view.TodoListView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The system boundary: turns UI intentions into Commands and Queries.
 *
 * Commands are awaited only until the Aggregate has validated them and the
 * event is in the store; read models update eventually (the UI hears about it
 * over SSE) — no {@code Thread.sleep} anywhere.
 */
@Invoker
public class TodoListInvoker extends InvokerWrapper {

    private static final long COMMAND_TIMEOUT_S = 30;

    @InvocationHandler
    public String createTodoList(String name, String user) throws Exception {
        var identifier = "TDLS_" + UUID.randomUUID();
        getCommandGateway()
                .send(new TodoListCreateCommand(identifier, name), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
        return identifier;
    }

    @InvocationHandler
    public void deleteTodoList(String identifier, String user) throws Exception {
        getCommandGateway()
                .send(new TodoListDeleteCommand(identifier), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

    @InvocationHandler
    public String addTodo(String identifier, String content, String user) throws Exception {
        var todoIdentifier = "TODO_" + UUID.randomUUID();
        getCommandGateway()
                .send(new TodoListAddTodoCommand(identifier, todoIdentifier, content), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
        return todoIdentifier;
    }

    @InvocationHandler
    public void checkTodo(String identifier, String todoIdentifier, String user) throws Exception {
        getCommandGateway()
                .send(new TodoListCheckTodoCommand(identifier, todoIdentifier), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

    @InvocationHandler
    public void removeTodo(String identifier, String todoIdentifier, String user) throws Exception {
        getCommandGateway()
                .send(new TodoListRemoveTodoCommand(identifier, todoIdentifier), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

    @InvocationHandler
    public CompletableFuture<TodoListView> findTodoListByIdentifier(String identifier) {
        return getQueryGateway().query(new TodoListViewFindByIdentifierQuery(identifier))
                .thenApply(Single::getData);
    }

    @InvocationHandler
    public CompletableFuture<Collection<TodoListListItemView>> searchTodoList(String nameLike, int page) {
        return getQueryGateway().query(new TodoListListItemViewSearchQuery(nameLike, page, 15))
                .thenApply(Multiple::getData);
    }

    private Metadata toUserMetadata(String user) {
        var m = new Metadata();
        m.put("user", user);
        return m;
    }
}
