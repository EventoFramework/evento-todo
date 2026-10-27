package com.eventoframework.demo.todo.api.user.command;

import com.evento.common.documentation.Domain;
import com.evento.common.modeling.messaging.payload.DomainCommand;
import com.evento.common.personaldata.DataSubject;
import com.evento.common.personaldata.PersonalData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A visitor picks a display name. The name is personal data: it travels and is stored encrypted. */
@Domain(name = "User")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRegisterCommand extends DomainCommand {

    // A pseudonymous id: the data subject, stored in clear
    @DataSubject
    private String userId;
    // Encrypted with the key of userId
    @PersonalData
    private String name;

    @Override
    public String getAggregateId() {
        return userId;
    }
}
