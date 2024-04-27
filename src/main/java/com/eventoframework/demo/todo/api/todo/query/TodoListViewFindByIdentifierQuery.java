package com.eventoframework.demo.todo.api.todo.query;

import com.eventoframework.demo.todo.api.todo.view.TodoListView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.Query;
import com.evento.common.modeling.messaging.query.Single;
@Domain(name = "TodoList")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListViewFindByIdentifierQuery
        implements Query<Single<TodoListView>> {
    private String identifier;
}
