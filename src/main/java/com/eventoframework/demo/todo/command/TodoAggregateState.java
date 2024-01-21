package com.eventoframework.demo.todo.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.evento.common.documentation.Domain;
import org.evento.common.modeling.state.AggregateState;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoAggregateState extends AggregateState {
    private boolean checked;
}
