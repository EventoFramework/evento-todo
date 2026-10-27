package com.eventoframework.demo.todo.service.invoker;

import com.evento.application.proxy.InvokerWrapper;
import com.evento.common.messaging.gateway.QueryOptions;
import com.evento.common.modeling.annotations.component.Invoker;
import com.evento.common.modeling.annotations.handler.InvocationHandler;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.evento.common.modeling.messaging.query.Page;
import com.evento.common.modeling.messaging.query.PageRequest;
import com.evento.common.modeling.messaging.query.Single;
import com.eventoframework.demo.todo.api.todo.command.TodoListAddTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCheckAllCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCheckTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCreateCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListDeleteCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListRemoveTodoCommand;
import com.eventoframework.demo.todo.api.todo.query.TodoListListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.todo.query.TodoListViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.todo.view.TodoListView;
import com.eventoframework.demo.todo.api.user.command.UserForgetCommand;
import com.eventoframework.demo.todo.api.user.command.UserRegisterCommand;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The system boundary: turns UI intentions into Commands and Queries.
 *
 * <p>Commands are awaited until the Aggregate has validated them and the event is stored. The
 * queries then ask to <b>read their writes</b> (Evento 3.0): they are answered only once the
 * projectors have processed every event this bundle wrote, so the page after a redirect always
 * shows what the visitor just did, with no retry loop and no {@code Thread.sleep}.
 */
@Invoker
public class TodoListInvoker extends InvokerWrapper {

    private static final long COMMAND_TIMEOUT_S = 30;
    private static final QueryOptions READ_YOUR_WRITES =
            QueryOptions.defaults().readYourWrites().timeout(Duration.ofSeconds(10));

    @InvocationHandler
    public String registerUser(String name) throws Exception {
        var userId = "USR_" + UUID.randomUUID();
        getCommandGateway()
                .send(new UserRegisterCommand(userId, name), toUserMetadata(userId))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
        return userId;
    }

    @InvocationHandler
    public void forgetUser(String userId) throws Exception {
        getCommandGateway()
                .send(new UserForgetCommand(userId), toUserMetadata(userId))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

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

    /** One command, one event per open todo (Evento 3.0 {@code DomainEvents}). */
    @InvocationHandler
    public void checkAllTodos(String identifier, String user) throws Exception {
        getCommandGateway()
                .send(new TodoListCheckAllCommand(identifier), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

    @InvocationHandler
    public void removeTodo(String identifier, String todoIdentifier, String user) throws Exception {
        getCommandGateway()
                .send(new TodoListRemoveTodoCommand(identifier, todoIdentifier), toUserMetadata(user))
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

    @InvocationHandler
    public TodoListView findTodoListByIdentifier(String identifier) throws Exception {
        Single<TodoListView> answer = getQueryGateway()
                .queryWith(new TodoListViewFindByIdentifierQuery(identifier), READ_YOUR_WRITES)
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
        return answer.getData();
    }

    @InvocationHandler
    public Page<TodoListListItemView> searchTodoList(String nameLike, int page, int size) throws Exception {
        return getQueryGateway()
                .queryWith(new TodoListListItemViewSearchQuery(nameLike, PageRequest.of(page, size)), READ_YOUR_WRITES)
                .get(COMMAND_TIMEOUT_S, TimeUnit.SECONDS);
    }

    private Metadata toUserMetadata(String user) {
        var m = new Metadata();
        m.put("user", user);
        return m;
    }
}
