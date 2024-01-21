package com.eventoframework.demo.todo.query.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Todo {
    @Id
    private String identifier;
    private String content;
    private boolean completed;
    private String createdBy;
    private String completedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime completedAt;

}
