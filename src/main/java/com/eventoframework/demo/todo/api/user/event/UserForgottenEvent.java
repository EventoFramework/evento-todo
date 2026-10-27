package com.eventoframework.demo.todo.api.user.event;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.ServiceEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The user's key is deleted: read models that copied the name in clear clean it on this event. */
@Domain(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserForgottenEvent extends ServiceEvent {

    private String userId;
}
