package com.eventoframework.demo.todo.command.user;

import com.evento.common.modeling.annotations.component.Aggregate;
import com.evento.common.modeling.annotations.handler.AggregateCommandHandler;
import com.evento.common.modeling.annotations.handler.EventSourcingHandler;
import com.eventoframework.demo.todo.api.user.command.UserRegisterCommand;
import com.eventoframework.demo.todo.api.user.event.UserRegisteredEvent;
import org.springframework.util.Assert;

@Aggregate
public class UserAggregate {

    @AggregateCommandHandler(init = true)
    public UserRegisteredEvent handle(UserRegisterCommand command) {
        Assert.isTrue(command.getName() != null && !command.getName().isBlank(),
                "Error: Name is empty");
        return new UserRegisteredEvent(command.getUserId(), command.getName().strip());
    }

    @EventSourcingHandler
    public UserAggregateState on(UserRegisteredEvent event) {
        // The state keeps nothing personal: rebuilding it never needs the name
        return new UserAggregateState();
    }
}
