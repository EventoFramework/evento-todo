package com.eventoframework.demo.todo.api.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.evento.common.documentation.Domain;
import org.evento.common.modeling.messaging.payload.View;

import java.time.ZonedDateTime;

@Domain(name = "Todo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoView extends View {
    private String identifier;
    private String content;
    private boolean completed;
    private String createdBy;
    private String completedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime completedAt;
}
