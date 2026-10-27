package com.eventoframework.demo.todo.web;

import com.evento.common.messaging.gateway.QueryGateway;
import com.evento.common.messaging.gateway.QueryOptions;
import com.evento.spring.boot.EventoBundleLifecycle;
import com.eventoframework.demo.todo.api.todo.query.TodoListExportQuery;
import com.eventoframework.demo.todo.realtime.SseUpdatesService;
import com.eventoframework.demo.todo.service.invoker.TodoListInvoker;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Server-rendered UI for the demo. Every POST here flows through the
 * {@link TodoListInvoker} — the RECQ boundary — and redirects back; the next
 * page reads its own writes, and other visitors' pages refresh over SSE.
 */
@Controller
public class PageController {

    private static final int PAGE_SIZE = 10;

    private final EventoBundleLifecycle evento;
    private final QueryGateway queries;
    private final SseUpdatesService sse;

    public PageController(EventoBundleLifecycle evento, QueryGateway queries, SseUpdatesService sse) {
        this.evento = evento;
        this.queries = queries;
        this.sse = sse;
    }

    /** The bundle is started by the starter once the context is ready. */
    private TodoListInvoker invoker() {
        return evento.bundle().getInvoker(TodoListInvoker.class);
    }

    /** The pseudonymous id that travels as Metadata "user": never the name itself. */
    private String user(HttpSession session) {
        return (String) session.getAttribute("userId");
    }

    private void addUser(HttpSession session, Model model) {
        model.addAttribute("user", session.getAttribute("userName"));
        model.addAttribute("userId", user(session));
    }

    @PostMapping("/user")
    public String setUser(@RequestParam String name, HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/", () -> {
            var userId = invoker().registerUser(name);
            session.setAttribute("userId", userId);
            session.setAttribute("userName", name.strip());
            return "redirect:/";
        });
    }

    @PostMapping("/user/forget")
    public String forgetUser(HttpSession session, RedirectAttributes redirect) {
        var userId = user(session);
        if (userId == null) return "redirect:/";
        return run(redirect, "/", () -> {
            invoker().forgetUser(userId);
            session.invalidate();
            redirect.addFlashAttribute("forgotten", userId);
            return "redirect:/";
        });
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "") String nameLike,
                        @RequestParam(defaultValue = "0") int page,
                        HttpSession session, Model model) throws Exception {
        addUser(session, model);
        model.addAttribute("nameLike", nameLike);
        if (user(session) != null) {
            model.addAttribute("lists", invoker().searchTodoList(nameLike, Math.max(page, 0), PAGE_SIZE));
        }
        return "index";
    }

    @GetMapping("/todo-list/{identifier}")
    public String list(@PathVariable String identifier,
                       HttpSession session, Model model) throws Exception {
        if (user(session) == null) return "redirect:/";
        // Read-your-writes: right after "create" the list is there, no retry needed
        model.addAttribute("list", invoker().findTodoListByIdentifier(identifier));
        addUser(session, model);
        return "list";
    }

    /**
     * Every list as CSV, streamed from the projection's cursor to the browser with the
     * {@link QueryGateway} bean of the Spring Boot starter.
     */
    @GetMapping("/export.csv")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=\"todo-lists.csv\"");
        PrintWriter out = response.getWriter();
        out.println("identifier,name,created_by,created_at,status,todos");
        // Views arrive while the rest is still travelling; leaving the loop (a browser that went
        // away) cancels the stream on both sides and closes the projection's cursor.
        try (var lists = queries.stream(new TodoListExportQuery(), QueryOptions.defaults().timeout(Duration.ofSeconds(30)))) {
            for (var l : lists) {
                out.println(String.join(",", csv(l.getIdentifier()), csv(l.getName()), csv(l.getCreatedBy()),
                        csv(l.getCreatedAt() == null ? null : l.getCreatedAt().toString()), csv(l.getStatus()),
                        String.valueOf(l.getTodos())));
                if (out.checkError()) break;
            }
        }
        out.flush();
    }

    private static String csv(String value) {
        if (value == null) return "";
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    @PostMapping("/todo-list")
    public String create(@RequestParam String name, HttpSession session,
                         RedirectAttributes redirect) {
        return run(redirect, "/", () -> {
            var id = invoker().createTodoList(name, user(session));
            return "redirect:/todo-list/" + id;
        });
    }

    @PostMapping("/todo-list/{identifier}/delete")
    public String delete(@PathVariable String identifier, HttpSession session,
                         RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker().deleteTodoList(identifier, user(session));
            return "redirect:/";
        });
    }

    @PostMapping("/todo-list/{identifier}/todo")
    public String addTodo(@PathVariable String identifier, @RequestParam String content,
                          HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker().addTodo(identifier, content, user(session));
            return "redirect:/todo-list/" + identifier;
        });
    }

    @PostMapping("/todo-list/{identifier}/todo/{todoIdentifier}/check")
    public String checkTodo(@PathVariable String identifier, @PathVariable String todoIdentifier,
                            HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker().checkTodo(identifier, todoIdentifier, user(session));
            return "redirect:/todo-list/" + identifier;
        });
    }

    @PostMapping("/todo-list/{identifier}/check-all")
    public String checkAll(@PathVariable String identifier, HttpSession session,
                           RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker().checkAllTodos(identifier, user(session));
            return "redirect:/todo-list/" + identifier;
        });
    }

    @PostMapping("/todo-list/{identifier}/todo/{todoIdentifier}/remove")
    public String removeTodo(@PathVariable String identifier, @PathVariable String todoIdentifier,
                             HttpSession session, RedirectAttributes redirect) {
        return run(redirect, "/todo-list/" + identifier, () -> {
            invoker().removeTodo(identifier, todoIdentifier, user(session));
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
