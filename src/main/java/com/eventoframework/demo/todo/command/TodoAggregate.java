package com.eventoframework.demo.todo.command;

import com.eventoframework.demo.todo.api.command.TodoDeleteCommand;
import com.eventoframework.demo.todo.api.command.TodoSetCheckedCommand;
import com.eventoframework.demo.todo.api.command.TodoCreateCommand;
import com.eventoframework.demo.todo.api.event.TodoCheckedSetEvent;
import com.eventoframework.demo.todo.api.event.TodoCreatedEvent;
import com.eventoframework.demo.todo.api.event.TodoDeletedEvent;
import org.evento.common.modeling.annotations.component.Aggregate;
import org.evento.common.modeling.annotations.handler.AggregateCommandHandler;
import org.evento.common.modeling.annotations.handler.EventSourcingHandler;
import org.springframework.util.Assert;

@Aggregate
public class TodoAggregate {

    @AggregateCommandHandler(init = true)
    public TodoCreatedEvent handle(TodoCreateCommand command){
        // Validation
        Assert.isTrue(command.getAggregateId() != null, "Error: Todo Id is null");
        Assert.isTrue(command.getContent() != null && !command.getContent().isBlank(), "Error: Content is empty");
        // Command is valid
        return new TodoCreatedEvent(command.getIdentifier(), command.getContent());
    }

    @EventSourcingHandler
    public TodoAggregateState on(TodoCreatedEvent event){
        var state = new TodoAggregateState();
        state.setChecked(false);
        return state;
    }

    @AggregateCommandHandler
    public TodoCheckedSetEvent handle(TodoSetCheckedCommand command, TodoAggregateState state){
        // Validation
        Assert.isTrue(state.isChecked() != command.isChecked(), "Error: Todo status not changed");
        // Command is valid
        return new TodoCheckedSetEvent(command.getIdentifier(), command.isChecked());
    }

    @EventSourcingHandler
    public void on(TodoCheckedSetEvent event, TodoAggregateState state){
        state.setChecked(event.isChecked());
    }

    @AggregateCommandHandler
    public TodoDeletedEvent handle(TodoDeleteCommand command, TodoAggregateState state){
        // Command is valid
        return new TodoDeletedEvent(command.getIdentifier());
    }

    @EventSourcingHandler
    public void on(TodoDeletedEvent event, TodoAggregateState state){
        state.setDeleted(true);
    }



}
