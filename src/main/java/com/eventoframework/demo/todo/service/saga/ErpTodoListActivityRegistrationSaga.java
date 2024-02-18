package com.eventoframework.demo.todo.service.saga;

import com.evento.common.messaging.gateway.CommandGateway;
import com.evento.common.modeling.annotations.component.Saga;
import com.evento.common.modeling.annotations.handler.SagaEventHandler;
import com.evento.common.modeling.messaging.message.application.Metadata;
import com.eventoframework.demo.todo.api.erp.command.ErpUserActivityRegisterCommand;
import com.eventoframework.demo.todo.api.erp.event.ErpUserActivityRegisteredEvent;
import com.eventoframework.demo.todo.api.todo.command.TodoListAddTodoCommand;
import com.eventoframework.demo.todo.api.todo.event.*;
import org.springframework.data.jpa.repository.Meta;

import java.util.HashMap;

@Saga(version = 1)
public class ErpTodoListActivityRegistrationSaga {

    @SagaEventHandler(init = true, associationProperty = "identifier")
    public ErpTodoListActivityRegistrationSagaState on(TodoListCreatedEvent event){
        var state = new ErpTodoListActivityRegistrationSagaState();
        state.setTodoIdentifier(event.getIdentifier());
        state.setToCheckCounter(0);
        state.setUsages(new HashMap<>());
        state.setAssociation("identifier", event.getIdentifier());
        return state;
    }

    @SagaEventHandler(associationProperty = "identifier")
    public void on(TodoListDeletedEvent event, ErpTodoListActivityRegistrationSagaState state){
        state.setEnded(true);
    }

    @SagaEventHandler(associationProperty = "identifier")
    public void on(TodoListTodoAddedEvent event,
                   ErpTodoListActivityRegistrationSagaState state,
                   Metadata metadata){
        state.setToCheckCounter(state.getToCheckCounter() + 1);
        var user = metadata.get("user");
        state.getUsages().put(user, state.getUsages().getOrDefault(user, 0) + 1);
    }

    @SagaEventHandler(associationProperty = "identifier")
    public void on(TodoListTodoRemovedEvent event, ErpTodoListActivityRegistrationSagaState state,
                   Metadata metadata){
        state.setToCheckCounter(state.getToCheckCounter() - 1);
        var user = metadata.get("user");
        state.getUsages().put(user, state.getUsages().getOrDefault(user, 0) + 1);
    }

    @SagaEventHandler(associationProperty = "identifier")
    public void on(TodoListTodoCheckedEvent event,
                   ErpTodoListActivityRegistrationSagaState state,
                   Metadata metadata,
                   CommandGateway commandGateway){
        var user = metadata.get("user");
        state.getUsages().put(user, state.getUsages().getOrDefault(user, 0) + 1);
        state.setToCheckCounter(state.getToCheckCounter() - 1);
        if(state.getToCheckCounter() == 0){
            state.setAssociation("resourceIdentifier", event.getIdentifier());
            commandGateway.send(new ErpUserActivityRegisterCommand(
                    "TodoList",
                    event.getIdentifier(),
                    state.getUsages()
            ));
        }
    }

    @SagaEventHandler(associationProperty = "resourceIdentifier")
    public void on(ErpUserActivityRegisteredEvent event, ErpTodoListActivityRegistrationSagaState state){
        state.setEnded(true);
    }
}
