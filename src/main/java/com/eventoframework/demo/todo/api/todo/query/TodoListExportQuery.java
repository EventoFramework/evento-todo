package com.eventoframework.demo.todo.api.todo.query;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.Query;
import com.evento.common.modeling.messaging.query.Streamed;
import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Every list, as a stream (Evento 3.0): the projection reads a database cursor only as fast as
 * the caller consumes, with flow control end to end; no hop holds the whole result.
 */
@Domain(name = "TodoList")
@NoArgsConstructor
@Getter
@Setter
public class TodoListExportQuery extends Query<Streamed<TodoListListItemView>> {
}
