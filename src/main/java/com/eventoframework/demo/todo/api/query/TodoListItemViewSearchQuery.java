package com.eventoframework.demo.todo.api.query;

import com.eventoframework.demo.todo.api.view.TodoListItemView;
import com.eventoframework.demo.todo.api.view.TodoView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.evento.common.documentation.Domain;
import org.evento.common.modeling.messaging.payload.Query;
import org.evento.common.modeling.messaging.query.Multiple;
import org.evento.common.modeling.messaging.query.Single;

@Domain(name = "Todo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListItemViewSearchQuery extends Query<Multiple<TodoListItemView>> {
    private String query;
    private int page;
    private int size;
}
