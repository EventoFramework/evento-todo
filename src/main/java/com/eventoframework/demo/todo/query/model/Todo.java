package com.eventoframework.demo.todo.query.model;

import com.eventoframework.demo.todo.api.view.TodoView;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Todo {
    private String identifier;
    private String content;
    private String createdBy;
    private String completedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime completedAt;

    public TodoView toView() {
        return new TodoView(
                identifier,
                content,
                completedAt != null,
                createdBy,
                completedBy,
                createdAt,
                completedAt
        );
    }
}
