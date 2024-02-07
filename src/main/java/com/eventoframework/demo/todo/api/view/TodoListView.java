package com.eventoframework.demo.todo.api.view;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.View;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;

@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListView implements View {
    private String identifier;
    private String content;
    private ArrayList<TodoView> todos;
    private String createdBy;
    private String updatedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
