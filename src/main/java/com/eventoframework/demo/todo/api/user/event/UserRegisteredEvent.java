package com.eventoframework.demo.todo.api.user.event;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainEvent;
import com.evento.common.personaldata.DataSubject;
import com.evento.common.personaldata.PersonalData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stored in the event store with {@code name} encrypted (AES-256-GCM, key of {@code userId}).
 * Once the user is forgotten the key is gone, and {@code name} reads as {@code null} everywhere,
 * replays and backups included: the event stays, the name does not.
 */
@Domain(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegisteredEvent extends DomainEvent {

    @DataSubject
    private String userId;
    @PersonalData
    private String name;
}
