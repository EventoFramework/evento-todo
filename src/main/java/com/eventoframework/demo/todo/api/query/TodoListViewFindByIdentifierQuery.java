package com.eventoframework.demo.todo.api.query;

import com.eventoframework.demo.todo.api.view.TodoListView;
import com.eventoframework.demo.todo.api.view.TodoView;
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
public class TodoListViewFindByIdentifierQuery implements Query<Single<TodoListView>> {
    private String identifier;
}
