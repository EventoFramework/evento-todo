package com.eventoframework.demo.todo.command.user;

import com.evento.common.personaldata.InMemorySubjectKeyProvider;
import com.evento.common.personaldata.SubjectKeys;
import com.evento.test.AggregateFixture;
import com.evento.test.ServiceFixture;
import com.eventoframework.demo.todo.api.user.command.UserForgetCommand;
import com.eventoframework.demo.todo.api.user.command.UserRegisterCommand;
import com.eventoframework.demo.todo.api.user.event.UserForgottenEvent;
import com.eventoframework.demo.todo.api.user.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void registeringKeepsTheNameWithoutSurroundingSpaces() {
        AggregateFixture.of(new UserAggregate())
                .givenNoPriorActivity()
                .when(new UserRegisterCommand("USR_1", "  Ada "))
                .expectEvent(new UserRegisteredEvent("USR_1", "Ada"));
    }

    @Test
    void forgettingAUserDeletesTheirKey() {
        var keys = new InMemorySubjectKeyProvider();
        SubjectKeys.install(keys);
        keys.key("USR_1", true);
        assertTrue(keys.key("USR_1", false).isPresent());

        ServiceFixture.of(new UserPrivacyService())
                .when(new UserForgetCommand("USR_1"))
                .expectEvent(new UserForgottenEvent("USR_1"));

        // The key is gone: every copy of the name, in any event or backup, is unreadable
        assertFalse(keys.key("USR_1", false).isPresent());
    }
}
