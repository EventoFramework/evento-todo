package com.eventoframework.demo.todo.command.user;

import com.evento.common.modeling.annotations.component.Service;
import com.evento.common.modeling.annotations.handler.CommandHandler;
import com.evento.common.personaldata.SubjectKeys;
import com.eventoframework.demo.todo.api.user.command.UserForgetCommand;
import com.eventoframework.demo.todo.api.user.event.UserForgottenEvent;

/**
 * Crypto-shredding: deleting the user's key makes every copy of their personal data unreadable,
 * in the event store, in snapshots and in backups, while the events themselves stay.
 */
@Service
public class UserPrivacyService {

    @CommandHandler
    public UserForgottenEvent handle(UserForgetCommand command) {
        SubjectKeys.forget(command.getUserId());
        return new UserForgottenEvent(command.getUserId());
    }
}
