package com.eventoframework.demo.todo.command.todo;

import com.evento.test.AggregateFixture;
import com.eventoframework.demo.todo.api.todo.command.TodoListAddTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCheckAllCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCheckTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCreateCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListDeleteCommand;
import com.eventoframework.demo.todo.api.todo.event.TodoListCreatedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListDeletedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoAddedEvent;
import com.eventoframework.demo.todo.api.todo.event.TodoListTodoCheckedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Given/when/then with the evento-test fixtures (Evento 3.0): the aggregate handlers run the
 * way a bundle runs them, with no server, no event store and no database.
 */
class TodoListAggregateTest {

    private static final String LIST = "TDLS_1";

    private static TodoListCreatedEvent created() {
        return new TodoListCreatedEvent(LIST, "Groceries");
    }

    private static TodoListTodoAddedEvent added(String todo) {
        return new TodoListTodoAddedEvent(LIST, todo, "todo " + todo);
    }

    @Test
    void createEmitsEventWithNameAndId() {
        AggregateFixture.of(new TodoListAggregate())
                .givenNoPriorActivity()
                .when(new TodoListCreateCommand(LIST, "Groceries"))
                .expectEvent(created());
    }

    @Test
    void createRequiresAName() {
        AggregateFixture.of(new TodoListAggregate())
                .givenNoPriorActivity()
                .when(new TodoListCreateCommand(LIST, "  "))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    void addTodoIsLimitedToFive() {
        AggregateFixture.of(new TodoListAggregate())
                .given(created(), added("A"), added("B"), added("C"), added("D"), added("E"))
                .when(new TodoListAddTodoCommand(LIST, "F", "one too many"))
                .expectException(IllegalArgumentException.class,
                        e -> assertEquals("Error: Todo list is full", e.getMessage()));
    }

    @Test
    void addingTheSameTodoTwiceIsRejected() {
        AggregateFixture.of(new TodoListAggregate())
                .given(created(), added("A"))
                .when(new TodoListAddTodoCommand(LIST, "A", "again"))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    void checkingTheLastOpenTodoCompletesTheList() {
        AggregateFixture.of(new TodoListAggregate())
                .given(created(), added("A"), added("B"), new TodoListTodoCheckedEvent(LIST, "A", false))
                .when(new TodoListCheckTodoCommand(LIST, "B"))
                .expectEvent(new TodoListTodoCheckedEvent(LIST, "B", true));
    }

    @Test
    void checkAllEmitsOneEventPerOpenTodo() {
        // DomainEvents: several events from one command, applied in order, stored all or none
        AggregateFixture.of(new TodoListAggregate())
                .given(created(), added("A"), added("B"), added("C"), new TodoListTodoCheckedEvent(LIST, "B", false))
                .when(new TodoListCheckAllCommand(LIST))
                .expectEvents(
                        new TodoListTodoCheckedEvent(LIST, "A", false),
                        new TodoListTodoCheckedEvent(LIST, "C", true))
                .expectState(TodoListAggregateState.class,
                        s -> assertEquals(3, s.getTodos().values().stream().filter(b -> b).count()));
    }

    @Test
    void checkAllNeedsAnOpenTodo() {
        AggregateFixture.of(new TodoListAggregate())
                .given(created(), added("A"), new TodoListTodoCheckedEvent(LIST, "A", true))
                .when(new TodoListCheckAllCommand(LIST))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    void aListWithACheckedTodoCannotBeDeleted() {
        AggregateFixture.of(new TodoListAggregate())
                .given(created(), added("A"), new TodoListTodoCheckedEvent(LIST, "A", true))
                .when(new TodoListDeleteCommand(LIST))
                .expectException(IllegalArgumentException.class);
    }

    @Test
    void anEmptyListCanBeDeleted() {
        AggregateFixture.of(new TodoListAggregate())
                .given(created())
                .when(new TodoListDeleteCommand(LIST))
                .expectEvent(new TodoListDeletedEvent(LIST));
    }
}
