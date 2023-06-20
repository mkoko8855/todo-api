package com.example.todo.todoapi.repository;

import com.example.todo.todoapi.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, String> { //uuid로하기로했으니..pk가 String이잖아.

}
