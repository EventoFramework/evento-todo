package com.eventoframework.demo.todo.api.todo.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.View;

import java.time.ZonedDateTime;

@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoView implements View {
    private String identifier;
    private String content;
    private boolean completed;
    private String createdBy;
    private String completedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime completedAt;
}
