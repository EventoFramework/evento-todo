package com.eventoframework.demo.todo.query;

import com.evento.common.modeling.annotations.component.Projector;
import com.evento.common.modeling.annotations.handler.EventHandler;
import com.evento.common.utils.ProjectorStatus;
import com.eventoframework.demo.todo.api.user.event.UserForgottenEvent;
import com.eventoframework.demo.todo.api.user.event.UserRegisteredEvent;
import com.eventoframework.demo.todo.query.model.DemoUser;
import com.eventoframework.demo.todo.query.model.DemoUserRepository;
import com.eventoframework.demo.todo.realtime.SseUpdatesService;

/**
 * Display names of the users. The handler sees the name decrypted; after the user is forgotten,
 * a replay of this projector reads {@code null} from the very same events (crypto-shredding),
 * and the live read model is cleaned on {@link UserForgottenEvent}.
 */
@Projector(version = 1)
public class UserDirectoryProjector {

    private final DemoUserRepository users;
    private final SseUpdatesService sse;

    public UserDirectoryProjector(DemoUserRepository users, SseUpdatesService sse) {
        this.users = users;
        this.sse = sse;
    }

    @EventHandler
    public void on(UserRegisteredEvent event) {
        users.save(new DemoUser(event.getUserId(), event.getName(), event.getName() == null));
    }

    @EventHandler
    public void on(UserForgottenEvent event, ProjectorStatus projectorStatus) {
        users.save(new DemoUser(event.getUserId(), null, true));
        if (projectorStatus.isHeadReached()) {
            sse.notifyEverything();
        }
    }
}
