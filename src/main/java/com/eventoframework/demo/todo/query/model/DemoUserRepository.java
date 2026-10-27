package com.eventoframework.demo.todo.query.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoUserRepository extends JpaRepository<DemoUser, String> {
}
