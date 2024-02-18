package com.eventoframework.demo.todo.service.saga;

import com.evento.common.modeling.annotations.component.Saga;
import com.evento.common.modeling.state.SagaState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor

public class ErpTodoListActivityRegistrationSagaState extends SagaState {
    private String todoIdentifier;
    private Integer toCheckCounter;
    private HashMap<String, Integer> usages;
}
