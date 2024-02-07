package com.eventoframework.demo.todo.web;

import com.eventoframework.demo.todo.api.view.TodoListListItemView;
import com.eventoframework.demo.todo.api.view.TodoListView;
import com.eventoframework.demo.todo.service.TodoInvoker;
import com.evento.application.EventoBundle;
import com.eventoframework.demo.todo.web.dto.CreatedResponse;
import com.eventoframework.demo.todo.web.dto.TodoCreateRequest;
import com.eventoframework.demo.todo.web.dto.TodoListCreateRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/todo")
public class TodoListController {

    private final TodoInvoker todoInvoker;

    public TodoListController(EventoBundle eventoBundle) {
        todoInvoker = eventoBundle.getInvoker(TodoInvoker.class);
    }

    @GetMapping("/")
    public CompletableFuture<Collection<TodoListListItemView>> searchTodoList(
            String query, int page
    ) {
        return todoInvoker.searchTodoList(query, page);
    }

    @GetMapping("/{identifier}")
    public CompletableFuture<TodoListView> findTodoListByIdentifier(
            @PathVariable String identifier) {
        return todoInvoker.findTodoListByIdentifier(identifier);
    }

    @PostMapping("/")
    public CreatedResponse createTodoList(
            @RequestBody TodoListCreateRequest request, @RequestHeader(name = "Authorization") String user) {
        return new CreatedResponse(todoInvoker.createTodoList(request.getName(), user));
    }


    @DeleteMapping("/{identifier}")
    public void deleteTodoList(
            @PathVariable String identifier,
            @RequestHeader(name = "Authorization") String user) {
        todoInvoker.deleteTodoList(identifier, user);
    }

    @PostMapping("/{identifier}/")
    public CreatedResponse addTodo(
            @PathVariable String identifier,
            @RequestBody TodoCreateRequest request,
            @RequestHeader(name = "Authorization") String user) {
        return new CreatedResponse(todoInvoker.addTodo(identifier, request.getContent(), user));
    }

    @DeleteMapping("/{identifier}/{todoIdentifier}")
    public void removeTodo(
            @PathVariable String identifier,
            @PathVariable String todoIdentifier,
            @RequestHeader(name = "Authorization") String user) {
        todoInvoker.removeTodo(identifier, todoIdentifier, user);
    }

    @PutMapping("/{identifier}/{todoIdentifier}")
    public void checkTodo(
            @PathVariable String identifier,
            @PathVariable String todoIdentifier,
            @RequestHeader(name = "Authorization") String user) {
        todoInvoker.checkTodo(identifier, todoIdentifier, user);
    }
}