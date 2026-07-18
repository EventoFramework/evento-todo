package com.eventoframework.demo.todo.realtime;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces the old MQTT plumbing with plain Server-Sent Events: the
 * {@code TodoListProjector} calls the notify methods as it materializes
 * events, and every open page reloads. In-JVM only — exactly what a
 * single-bundle demo needs, with zero extra infrastructure.
 */
@Service
public class SseUpdatesService {

    private static final long TIMEOUT_MS = 30 * 60 * 1000L;

    private final Set<SseEmitter> globalListeners = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<SseEmitter>> listListeners = new ConcurrentHashMap<>();

    public SseEmitter subscribeToAllLists() {
        return register(globalListeners);
    }

    public SseEmitter subscribeToList(String listId) {
        return register(listListeners.computeIfAbsent(listId, k -> ConcurrentHashMap.newKeySet()));
    }

    public void notifyListChanged(String listId) {
        broadcast(globalListeners, listId);
        var listeners = listListeners.get(listId);
        if (listeners != null) broadcast(listeners, listId);
    }

    private SseEmitter register(Set<SseEmitter> target) {
        var emitter = new SseEmitter(TIMEOUT_MS);
        target.add(emitter);
        emitter.onCompletion(() -> target.remove(emitter));
        emitter.onTimeout(() -> target.remove(emitter));
        emitter.onError(e -> target.remove(emitter));
        return emitter;
    }

    private void broadcast(Set<SseEmitter> listeners, String listId) {
        for (var emitter : listeners) {
            try {
                emitter.send(SseEmitter.event().name("message").data(listId));
            } catch (Exception e) {
                listeners.remove(emitter);
            }
        }
    }
}
