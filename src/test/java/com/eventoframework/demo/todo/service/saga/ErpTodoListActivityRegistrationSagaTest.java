package com.eventoframework.demo.todo.service.saga;

import com.evento.test.SagaFixture;
import com.eventoframework.demo.todo.api.erp.command.ErpUserActivityRegisterCommand;
import com.eventoframework.demo.todo.api.erp.event.ErpUserActivityRegisteredEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListCreatedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoAddedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoCheckedEvent;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErpTodoListActivityRegistrationSagaTest {

    @Test
    void theLastCheckRegistersTheActivity() {
        SagaFixture.of(new ErpTodoListActivityRegistrationSaga())
                .given(new TodoListCreatedEvent("TDLS_1", "Groceries"),
                        new TodoListTodoAddedEvent("TDLS_1", "A", "milk"))
                .whenEvent(new TodoListTodoCheckedEvent("TDLS_1", "A", true))
                .expectCommand(ErpUserActivityRegisterCommand.class, c -> {
                    assertEquals("TodoList", c.getResourceType());
                    assertEquals("TDLS_1", c.getResourceIdentifier());
                })
                .expectAssociation("resourceIdentifier", "TDLS_1")
                .expectNotEnded();
    }

    @Test
    void aCheckThatLeavesOpenTodosRegistersNothing() {
        SagaFixture.of(new ErpTodoListActivityRegistrationSaga())
                .given(new TodoListCreatedEvent("TDLS_1", "Groceries"),
                        new TodoListTodoAddedEvent("TDLS_1", "A", "milk"),
                        new TodoListTodoAddedEvent("TDLS_1", "B", "bread"))
                .whenEvent(new TodoListTodoCheckedEvent("TDLS_1", "A", false))
                .expectNoCommands();
    }

    @Test
    void theRegistrationEndsTheSaga() {
        SagaFixture.of(new ErpTodoListActivityRegistrationSaga())
                .given(new TodoListCreatedEvent("TDLS_1", "Groceries"),
                        new TodoListTodoAddedEvent("TDLS_1", "A", "milk"),
                        new TodoListTodoCheckedEvent("TDLS_1", "A", true))
                .whenEvent(new ErpUserActivityRegisteredEvent("TodoList", "TDLS_1", new HashMap<>()))
                .expectEnded();
    }
}
