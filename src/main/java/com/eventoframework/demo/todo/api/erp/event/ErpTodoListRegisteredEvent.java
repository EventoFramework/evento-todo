package com.eventoframework.demo.todo.api.erp.event;

import com.evento.common.modeling.messaging.payload.ServiceEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErpTodoListRegisteredEvent extends ServiceEvent {
    private String todoListIdentifier;
    private HashMap<String, Integer> contributions;


}
