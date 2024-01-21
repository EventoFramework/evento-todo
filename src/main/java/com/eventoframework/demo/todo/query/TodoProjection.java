package com.eventoframework.demo.todo.query;

import com.eventoframework.demo.todo.api.query.TodoListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.query.TodoViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.view.TodoListItemView;
import com.eventoframework.demo.todo.api.view.TodoView;
import com.eventoframework.demo.todo.query.model.TodoRepository;
import org.evento.common.modeling.annotations.component.Projection;
import org.evento.common.modeling.annotations.handler.QueryHandler;
import org.evento.common.modeling.messaging.query.Multiple;
import org.evento.common.modeling.messaging.query.Single;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;

@Projection()
public class TodoProjection {

    private final TodoRepository repository;

    public TodoProjection(TodoRepository repository) {
        this.repository = repository;
    }

    @QueryHandler
    public Single<TodoView> handle(TodoViewFindByIdentifierQuery query){
        return Single.of(repository.findById(query.getIdentifier()).map(t -> new TodoView(t.getIdentifier(),
                t.getContent(),
                t.isCompleted(),
                t.getCreatedBy(),
                t.getCompletedBy(),
                t.getCreatedAt(),
                t.getCompletedAt())).orElseThrow());
    }

    @QueryHandler
    public Multiple<TodoListItemView> handle(TodoListItemViewSearchQuery query){
        return Multiple.of(repository.search("%" + query.getQuery() + "%", PageRequest.of(query.getPage(), query.getSize()))
                .map(t ->new TodoListItemView(t.getIdentifier(), t.getContent(), t.isCompleted())).toList());
    }
}