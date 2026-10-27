package com.eventoframework.demo.todo.api.todo.query;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.Query;
import com.evento.common.modeling.messaging.query.Page;
import com.evento.common.modeling.messaging.query.PageRequest;
import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Evento 3.0 paged answer: one slice of the lists, with the total for the pager. */
@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListListItemViewSearchQuery
        extends Query<Page<TodoListListItemView>> {
    // A like filter for the TodoList name
    private String nameLike;
    private PageRequest page;
}
