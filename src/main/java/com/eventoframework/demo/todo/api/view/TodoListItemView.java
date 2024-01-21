package com.eventoframework.demo.todo.api.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.evento.common.documentation.Domain;
import org.evento.common.modeling.messaging.payload.View;

@Domain(name = "Todo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListItemView extends View {
    private String identifier;
    private String content;
    private boolean completed;
}
