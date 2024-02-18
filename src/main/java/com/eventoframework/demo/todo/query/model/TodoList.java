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

    public TodoListView toView() {
        return new TodoListView(getIdentifier(),
                getName(),
                new ArrayList<>(getTodos().stream().map(Todo::toView).toList()),
                getCreatedBy(),
                getUpdatedBy(),
                getCreatedAt(),
                getUpdatedAt());
    }

    public TodoListListItemView toListItemView() {
        return new TodoListListItemView(getIdentifier(), getName());
    }
}
