package com.eventoframework.demo.todo.command.todo;

import com.eventoframework.demo.todo.api.todo.command.TodoListAddTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCheckTodoCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListCreateCommand;
import com.eventoframework.demo.todo.api.todo.command.TodoListDeleteCommand;
import com.eventoframework.demo.todo.api.todo.event.TodoListCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RECQ aggregates are plain Java: handlers are directly testable without any
 * framework infrastructure — feed commands, apply events, assert behavior.
 */
class TodoListAggregateTest {

    private TodoListAggregate aggregate;
    private TodoListAggregateState state;

    @BeforeEach
    void setUp() {
        aggregate = new TodoListAggregate();
        var created = aggregate.handle(new TodoListCreateCommand("TDLS_1", "Groceries"));
        state = aggregate.on(created);
    }

    @Test
    void createEmitsEventWithNameAndId() {
        var event = aggregate.handle(new TodoListCreateCommand("TDLS_2", "Chores"));
        assertNotNull(event);
        assertEquals("TDLS_2", event.getIdentifier());
        assertEquals("Chores", event.getContent());
    }

    @Test
    void createRequiresAName() {
        assertThrows(IllegalArgumentException.class,
                () -> aggregate.handle(new TodoListCreateCommand("TDLS_3", "  ")));
    }

    @Test
    void addTodoIsLimitedToFive() {
        for (int i = 0; i < 5; i++) {
            var event = aggregate.handle(
                    new TodoListAddTodoCommand("TDLS_1", "TODO_" + i, "todo " + i), state);
            aggregate.on(event, state);
        }
        assertEquals(5, state.getTodos().size());
        assertThrows(IllegalArgumentException.class, () -> aggregate.handle(
                new TodoListAddTodoCommand("TDLS_1", "TODO_6", "one too many"), state));
    }

    @Test
    void addingTheSameTodoTwiceIsRejected() {
        var event = aggregate.handle(new TodoListAddTodoCommand("TDLS_1", "TODO_A", "buy milk"), state);
        aggregate.on(event, state);
        assertThrows(IllegalArgumentException.class, () -> aggregate.handle(
                new TodoListAddTodoCommand("TDLS_1", "TODO_A", "buy milk again"), state));
    }

    @Test
    void checkingTheLastTodoFlagsAllChecked() {
        aggregate.on(aggregate.handle(new TodoListAddTodoCommand("TDLS_1", "TODO_A", "a"), state), state);
        aggregate.on(aggregate.handle(new TodoListAddTodoCommand("TDLS_1", "TODO_B", "b"), state), state);

        var first = aggregate.handle(new TodoListCheckTodoCommand("TDLS_1", "TODO_A"), state);
        aggregate.on(first, state);
        assertTrue(!first.isAllChecked());

        var last = aggregate.handle(new TodoListCheckTodoCommand("TDLS_1", "TODO_B"), state);
        assertTrue(last.isAllChecked());
    }

    @Test
    void deleteIsRejectedWhenATodoIsChecked() {
        aggregate.on(aggregate.handle(new TodoListAddTodoCommand("TDLS_1", "TODO_A", "a"), state), state);
        aggregate.on(aggregate.handle(new TodoListCheckTodoCommand("TDLS_1", "TODO_A"), state), state);
        assertThrows(IllegalArgumentException.class,
                () -> aggregate.handle(new TodoListDeleteCommand("TDLS_1"), state));
    }
}
