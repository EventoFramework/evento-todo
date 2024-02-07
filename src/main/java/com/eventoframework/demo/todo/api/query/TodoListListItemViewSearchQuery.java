package com.eventoframework.demo.todo.api.query;

import com.eventoframework.demo.todo.api.view.TodoListListItemView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.Query;
import com.evento.common.modeling.messaging.query.Multiple;

@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListListItemViewSearchQuery implements Query<Multiple<TodoListListItemView>> {
    private String query;
    private int page;
    private int size;
}
