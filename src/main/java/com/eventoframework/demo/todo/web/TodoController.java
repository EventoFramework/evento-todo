package com.eventoframework.demo.todo.web;

import com.eventoframework.demo.todo.api.view.TodoListItemView;
import com.eventoframework.demo.todo.api.view.TodoView;
import com.eventoframework.demo.todo.service.TodoInvoker;
import org.evento.application.EventoBundle;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoInvoker todoInvoker;

    public TodoController(EventoBundle eventoBundle) {
        todoInvoker = eventoBundle.getInvoker(TodoInvoker.class);
    }

    @GetMapping("/")
    public CompletableFuture<Collection<TodoListItemView>> searchTodo(
            String query,
            int page){
        return todoInvoker.searchTodo(query, page);
    }

    @GetMapping("/{identifier}")
    public CompletableFuture<TodoView> findTodoByIdentifier(@PathVariable String identifier){
        return todoInvoker.findTodoByIdentifier(identifier);
    }

    @PostMapping("/")
    public TodoCreatedResponse createTodo(
            @RequestBody TodoCreateRequest request,
            @RequestHeader(name = "Authorization") String user){
        var identifier = UUID.randomUUID().toString();
        todoInvoker.createTodo(identifier, request.getContent(), user);
        return new TodoCreatedResponse(identifier);
    }

    @PutMapping("/{identifier}")
    public void checkTodo(
            @PathVariable String identifier,
            boolean checked,
            @RequestHeader(name = "Authorization") String user ){
        todoInvoker.checkTodo(identifier, checked, user);
    }

    @DeleteMapping("/{identifier}")
    public void deleteTodo(
            @PathVariable String identifier,
            @RequestHeader(name = "Authorization") String user ){
        todoInvoker.deleteTodo(identifier, user);
    }
}
