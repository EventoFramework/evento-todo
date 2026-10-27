package com.eventoframework.demo.todo.query.model;

import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.todo.view.TodoListView;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoList {
    @Id
    private String identifier;
    private String name;
    private String createdBy;
    private String updatedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    @ElementCollection(fetch=FetchType.EAGER)
    private List<Todo> todos;
    private ZonedDateTime registeredAt;
    private TodoListStatus status;

    /** {@code names} turns the pseudonymous user ids of the events into display names. */
    public TodoListView toView(UnaryOperator<String> names) {
        return new TodoListView(getIdentifier(),
                getName(),
                new ArrayList<>(getTodos().stream().map(t -> t.toView(names)).toList()),
                names.apply(getCreatedBy()),
                names.apply(getUpdatedBy()),
                getCreatedAt(),
                getUpdatedAt(),
                getStatus() == null ? null : getStatus().name());
    }

    public TodoListListItemView toListItemView(UnaryOperator<String> names) {
        return new TodoListListItemView(getIdentifier(), getName(), names.apply(getCreatedBy()), getCreatedAt(),
                getStatus() == null ? null : getStatus().name(), getTodos().size());
    }

}
