package com.eventoframework.demo.todo.web;

import com.evento.application.EventoBundle;
import com.eventoframework.demo.todo.realtime.SseUpdatesService;
import com.eventoframework.demo.todo.service.invoker.TodoListInvoker;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 * Server-rendered UI for the demo. Every POST here flows through the
 * {@link TodoListInvoker} — the RECQ boundary — and redirects back; pages then
 * refresh themselves over SSE as the Projector catches up.
 */
@Controller
public class PageController {

    private final TodoListInvoker invoker;
    private final SseUpdatesService sse;

    public PageController(EventoBundle eventoBundle, SseUpdatesService sse) {
        this.invoker = eventoBundle.getInvoker(TodoListInvoker.class);
        this.sse = sse;
    }

    private String user(HttpSession session) {
        return (String) session.getAttribute("user");
    }

    @PostMapping("/user")
    public String setUser(@RequestParam String name, HttpSession session) {
        session.setAttribute("user", name.strip());
        return "redirect:/";
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "") String nameLike,
                        HttpSession session, Model model) throws Exception {
        model.addAttribute("user", user(session));
        model.addAttribute("nameLike", nameLike);
        if (user(session) != null) {
            model.addAttribute("lists",
                    invoker.searchTodoList(nameLike, 0).get(15, TimeUnit.SECONDS));
        }
        return "index";
    }

    @GetMapping("/todo-list/{identifier}")
    public String list(@PathVariable String identifier,
                       HttpSession session, Model model) throws Exception {
        if (user(session) == null) return "redirect:/";
        // The read model is eventually consistent: right after "create" the
        // projection may not exist yet — retry briefly instead of failing.
        Exception last = null;
        for (int attempt = 0; attempt < 10; attempt++) {
            try {
                model.addAttribute("list",
                        invoker.findTodoListByIdentifier(identifier).get(15, TimeUnit.SECONDS));
                model.addAttribute("user", user(session));
                return "list";
            } catch (Exception e) {
                last = e;
                Thread.sleep(200);
            }
        }
        throw new NoSuchElementException("Todo list not found: " + identifier, last);
    }

    @PostMapping("/todo-list")
    public String create(@RequestParam String name, HttpSession session,
                         RedirectAttributes redirect) {
        return run(redirect, "/", () -> {
            var id = invoker.createTodoList(name, user(session));
            return "redirect:/todo-list/" + id;
        });
    }

    @PostMapping("/todo-list/{identifier}/delete")
    public String delete(@PathVariable String identifier, HttpSession session,
                         RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker.deleteTodoList(identifier, user(session));
            return "redirect:/";
        });
    }

    @PostMapping("/todo-list/{identifier}/todo")
    public String addTodo(@PathVariable String identifier, @RequestParam String content,
                          HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker.addTodo(identifier, content, user(session));
            return "redirect:/todo-list/" + identifier;
        });
    }

    @PostMapping("/todo-list/{identifier}/todo/{todoIdentifier}/check")
    public String checkTodo(@PathVariable String identifier, @PathVariable String todoIdentifier,
                            HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker.checkTodo(identifier, todoIdentifier, user(session));
            return "redirect:/todo-list/" + identifier;
        });
    }

    @PostMapping("/todo-list/{identifier}/todo/{todoIdentifier}/remove")
    public String removeTodo(@PathVariable String identifier, @PathVariable String todoIdentifier,
                             HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker.removeTodo(identifier, todoIdentifier, user(session));
            return "redirect:/todo-list/" + identifier;
        });
    }

    @GetMapping("/sse/todo-lists")
    public SseEmitter sseAll() {
        return sse.subscribeToAllLists();
    }

    @GetMapping("/sse/todo-list/{identifier}")
    public SseEmitter sseList(@PathVariable String identifier) {
        return sse.subscribeToList(identifier);
    }

    /**
     * Domain validation errors (e.g. "Todo list is full") surface as command
     * failures — show them to the visitor instead of a 500 page.
     */
    private String run(RedirectAttributes redirect, String fallback, ThrowingAction action) {
        try {
            return action.run();
        } catch (Exception e) {
            var cause = e.getCause() != null ? e.getCause() : e;
            redirect.addFlashAttribute("error", cause.getMessage());
            return "redirect:" + fallback;
        }
    }

    @FunctionalInterface
    private interface ThrowingAction {
        String run() throws Exception;
    }
}
