package com.eventoframework.demo.todo.command.todo;

import com.evento.common.modeling.annotations.component.Aggregate;
import com.evento.common.modeling.annotations.handler.AggregateCommandHandler;
import com.evento.common.modeling.annotations.handler.EventSourcingHandler;
import com.eventoframework.demo.todo.api.todo.command.*;
import com.eventoframework.demo.todo.api.todo.event.*;
import org.springframework.util.Assert;

import java.util.HashMap;

@Aggregate
public class TodoListAggregate {

    @AggregateCommandHandler(init = true)
    public TodoListCreatedEvent handle(TodoListCreateCommand command){
        // Validation
        Assert.isTrue(command.getAggregateId() != null,
                "Error: Todo Id is null");
        Assert.isTrue(command.getName() != null && !command.getName().isBlank(),
                "Error: Content is empty");
        // Command is valid
        return new TodoListCreatedEvent(command.getIdentifier(), command.getName());
    }

    @EventSourcingHandler
    public TodoListAggregateState on(TodoListCreatedEvent event){
        var state = new TodoListAggregateState();
        state.setTodos(new HashMap<>());
        return state;
    }

    @AggregateCommandHandler
    public TodoListDeletedEvent handle(TodoListDeleteCommand command, TodoListAggregateState state){
        // Validation
        Assert.isTrue(state.getTodos().values().stream().noneMatch(a -> a),
                "Error: List contains a checked todo");

        // Command is valid
        return new TodoListDeletedEvent(command.getIdentifier());
    }

    @EventSourcingHandler
    public void on(TodoListDeletedEvent event, TodoListAggregateState state){
        state.setDeleted(true);
    }

    @AggregateCommandHandler
    public TodoListTodoAddedEvent handle(TodoListAddTodoCommand command, TodoListAggregateState state){
        // Command Validation
        Assert.isTrue(command.getTodoIdentifier() != null && !command.getTodoIdentifier().isBlank(),
                "Error: Invalid todo identifier");
        Assert.isTrue(command.getContent() != null && !command.getContent().isBlank(),
                "Error: Invalid todo content");
        // State Validation
        Assert.isTrue(!state.getTodos().containsKey(command.getTodoIdentifier()),
                "Error: Todo already present");
        Assert.isTrue(state.getTodos().size() < 5,
                "Error: Todo list is full");
        // Command is valid
        return new TodoListTodoAddedEvent(
                command.getIdentifier(),
                command.getTodoIdentifier(),
                command.getContent());
    }

    @EventSourcingHandler
    public void on(TodoListTodoAddedEvent event, TodoListAggregateState state){
        state.getTodos().put(event.getTodoIdentifier(), false);
    }

    @AggregateCommandHandler
    public TodoListTodoRemovedEvent handle(TodoListRemoveTodoCommand command, TodoListAggregateState state){
        // Validation
        Assert.isTrue(state.getTodos().containsKey(command.getTodoIdentifier()),
                "Error: Todo not present");
        Assert.isTrue(!state.getTodos().get(command.getTodoIdentifier()),
                "Error: Todo already checked");
        // Command is valid
        return new TodoListTodoRemovedEvent(
                command.getIdentifier(),
                command.getTodoIdentifier());
    }

    @EventSourcingHandler
    public void on(TodoListTodoRemovedEvent event, TodoListAggregateState state){
        state.getTodos().remove(event.getTodoIdentifier());
    }

    @AggregateCommandHandler
    public TodoListTodoCheckedEvent handle(TodoListCheckTodoCommand command, TodoListAggregateState state){
        // Validation
        Assert.isTrue(state.getTodos().containsKey(command.getTodoIdentifier()),
                "Error: Todo not present");
        Assert.isTrue(!state.getTodos().get(command.getTodoIdentifier()),
                "Error: Todo already checked");
        // Command is valid
        return new TodoListTodoCheckedEvent(
                command.getIdentifier(),
                command.getTodoIdentifier());
    }

    @EventSourcingHandler
    public void on(TodoListTodoCheckedEvent event, TodoListAggregateState state){
        state.getTodos().put(event.getTodoIdentifier(), true);
    }





}
