package com.eventoframework.demo.todo.command.todo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.evento.common.modeling.state.AggregateState;

import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TodoListAggregateState extends AggregateState {
    private HashMap<String, Boolean> todos;
}
