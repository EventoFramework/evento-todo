package com.eventoframework.demo.todo.query.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The read model's copy of a user's display name. It is a copy in clear, so it is the
 * application's to clean when the user is forgotten (the event store needs nothing: its copy is
 * encrypted with a key that no longer exists).
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DemoUser {

    public static final String FORGOTTEN = "forgotten user";

    @Id
    private String id;
    private String name;
    private boolean forgotten;

    public String displayName() {
        return forgotten || name == null ? FORGOTTEN : name;
    }
}
