package com.eventoframework.demo.todo.query.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, String> {
    @Query("select t from Todo t where t.content like ?1")
    Page<Todo> search(String query, PageRequest pa);
}