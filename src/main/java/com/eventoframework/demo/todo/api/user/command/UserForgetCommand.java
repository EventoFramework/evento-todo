package com.eventoframework.demo.todo.api.user.command;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.ServiceCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The right to erasure: forget everything personal about this user. */
@Domain(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserForgetCommand extends ServiceCommand {

    private String userId;

    @Override
    public String getLockId() {
        return userId;
    }
}
