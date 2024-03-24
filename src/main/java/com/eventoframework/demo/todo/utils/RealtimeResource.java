package com.eventoframework.demo.todo.utils;

import javax.validation.constraints.NotNull;

public interface RealtimeResource {

    @NotNull
    String getIdentifier();

    @NotNull
    default String getAggregate() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    default String getTopic() {
        return getAggregate() + "/" + getIdentifier();
    }
}
