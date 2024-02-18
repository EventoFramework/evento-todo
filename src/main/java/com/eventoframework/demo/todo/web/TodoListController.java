package com.eventoframework.demo.todo.web;

import com.eventoframework.demo.todo.api.todo.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.todo.view.TodoListView;
import com.eventoframework.demo.todo.service.TodoInvoker;
import com.evento.application.EventoBundle;
import com.eventoframework.demo.todo.web.dto.CreatedResponse;
import com.eventoframework.demo.todo.web.dto.TodoCreateRequest;
import com.eventoframework.demo.todo.web.dto.TodoListCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/todo-list")
public class TodoListController {

    private final TodoInvoker todoInvoker;

    public TodoListController(EventoBundle eventoBundle) {
        todoInvoker = eventoBundle.getInvoker(TodoInvoker.class);
    }

    @GetMapping("/")
    public CompletableFuture<ResponseEntity<Collection<TodoListListItemView>>> searchTodoList(
            String query, int page
    ) {
        return todoInvoker.searchTodoList(query, page).thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{identifier}")
    public CompletableFuture<ResponseEntity<TodoListView>> findTodoListByIdentifier(
            @PathVariable String identifier) {
        return todoInvoker.findTodoListByIdentifier(identifier).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/")
    public ResponseEntity<CreatedResponse> createTodoList(
            @RequestBody TodoListCreateRequest request, @RequestHeader(name = "Authorization") String user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatedResponse(todoInvoker.createTodoList(request.getName(), user)));
    }


    @DeleteMapping("/{identifier}")
    public ResponseEntity<Void> deleteTodoList(
            @PathVariable String identifier,
            @RequestHeader(name = "Authorization") String user) {
        todoInvoker.deleteTodoList(identifier, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{identifier}/todo/")
    public ResponseEntity<CreatedResponse> addTodo(
            @PathVariable String identifier,
            @RequestBody TodoCreateRequest request,
            @RequestHeader(name = "Authorization") String user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatedResponse(todoInvoker.addTodo(identifier, request.getContent(), user)));
    }

    @DeleteMapping("/{identifier}/todo/{todoIdentifier}")
    public ResponseEntity<Void> removeTodo(
            @PathVariable String identifier,
            @PathVariable String todoIdentifier,
            @RequestHeader(name = "Authorization") String user) {
        todoInvoker.removeTodo(identifier, todoIdentifier, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{identifier}/todo/{todoIdentifier}")
    public ResponseEntity<Void> checkTodo(
            @PathVariable String identifier,
            @PathVariable String todoIdentifier,
            @RequestHeader(name = "Authorization") String user) {
        todoInvoker.checkTodo(identifier, todoIdentifier, user);
        return ResponseEntity.accepted().build();
    }
}