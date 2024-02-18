package com.eventoframework.demo.todo.api.erp.command;

import com.evento.common.modeling.messaging.payload.ServiceCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErpRegisterTodoListCommand implements ServiceCommand {

    private String todoListIdentifier;
    private HashMap<String, Integer> contributions;

    @Override
    public String getLockId() {
        return "ERPTDL_" + todoListIdentifier;
    }
}
