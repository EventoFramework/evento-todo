package com.eventoframework.demo.todo.query;

import com.evento.common.modeling.annotations.component.Projection;
import com.evento.common.modeling.annotations.handler.QueryHandler;
import com.evento.common.modeling.messaging.query.Page;
import com.evento.common.modeling.messaging.query.Single;
import com.evento.common.modeling.messaging.query.Streamed;
import com.eventoframework.demo.todo.api.todo.query.TodoListExportQuery;
import com.eventoframework.demo.todo.api.todo.query.TodoListListItemViewSearchQuery;
import com.eventoframework.demo.todo.api.todo.query.TodoListViewFindByIdentifierQuery;
import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.todo.view.TodoListView;
import com.eventoframework.demo.todo.query.model.DemoUser;
import com.eventoframework.demo.todo.query.model.DemoUserRepository;
import com.eventoframework.demo.todo.query.model.TodoList;
import com.eventoframework.demo.todo.query.model.TodoListRepository;
import com.eventoframework.demo.todo.query.model.TodoListStatus;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;

@Projection
public class TodoListProjection {

    private final TodoListRepository repository;
    private final DemoUserRepository users;
    private final JdbcTemplate jdbc;

    public TodoListProjection(TodoListRepository repository, DemoUserRepository users, JdbcTemplate jdbc) {
        this.repository = repository;
        this.users = users;
        this.jdbc = jdbc;
    }

    @QueryHandler
    public Single<TodoListView> handle(TodoListViewFindByIdentifierQuery query) {
        return Single.of(repository.findById(query.getIdentifier())
                .map(l -> l.toView(this::displayName)).orElseThrow());
    }

    /** Evento 3.0 {@code Page}: the requested slice, and the total for the pager. */
    @QueryHandler
    public Page<TodoListListItemView> handle(TodoListListItemViewSearchQuery query) {
        var request = query.getPage();
        var page = repository.search("%" + query.getNameLike() + "%",
                org.springframework.data.domain.PageRequest.of(request.getNumber(), request.getSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt")));
        return Page.of(page.map(l -> l.toListItemView(this::displayName)).toList(),
                request.getNumber(), request.getSize(), page.getTotalElements());
    }

    /**
     * Evento 3.0 {@code Streamed}: the source is a database cursor opened only when the caller
     * streams, read as frames are sent, closed at the end (or on cancel). queryForStream holds its
     * own connection until the stream is closed, so no transaction is needed around it.
     */
    @QueryHandler
    public Streamed<TodoListListItemView> handle(TodoListExportQuery query) {
        return Streamed.of(() -> jdbc.queryForStream("""
                        select l.identifier, l.name, l.created_by, l.created_at, l.status,
                               u.name as user_name, u.forgotten,
                               (select count(*) from todo_list_todos t where t.todo_list_identifier = l.identifier) as todos
                        from todo_list l left join demo_user u on u.id = l.created_by
                        order by l.created_at""",
                (rs, i) -> {
                    var createdAt = rs.getObject("created_at", OffsetDateTime.class);
                    var status = rs.getObject("status") == null ? null : TodoListStatus.values()[rs.getInt("status")].name();
                    String createdBy = rs.getString("created_by");
                    if (rs.getString("user_name") != null || rs.getBoolean("forgotten")) {
                        createdBy = rs.getBoolean("forgotten") ? DemoUser.FORGOTTEN : rs.getString("user_name");
                    }
                    return new TodoListListItemView(rs.getString("identifier"), rs.getString("name"), createdBy,
                            createdAt == null ? null : createdAt.toZonedDateTime(), status, rs.getInt("todos"));
                }));
    }

    private String displayName(String userId) {
        if (userId == null) return null;
        return users.findById(userId).map(DemoUser::displayName).orElse(userId);
    }
}
