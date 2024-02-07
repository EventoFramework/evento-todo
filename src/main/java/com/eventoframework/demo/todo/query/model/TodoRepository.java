package com.eventoframework.demo.todo.query.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends JpaRepository<TodoList, String> {
    @Query("select t from TodoList t where t.content like ?1")
    Page<TodoList> search(String query, PageRequest pa);
}