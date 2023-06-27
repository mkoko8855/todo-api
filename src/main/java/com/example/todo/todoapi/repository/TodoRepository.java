package com.example.todo.todoapi.repository;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> { //uuid로하기로했으니..pk가 String이잖아.

        //특정 회원의 할 일 목록 리턴 0626
        @Query("SELECT t FROM Todo t WHERE t.user = :user") //테이블이 아닌 엔터티기준의다 JSQL은.
        List<Todo> findAllByUser(@Param("user") User user); //유저를받겠다. 이름이 같으니 User앞에 Param은생략해도됨.
        //즉, 유저가 누구냐에 따라 todo목록이 달라지겠지.



        //회원이 작성한 일정의 개수를 리턴
        @Query("SELECT COUNT(*) FROM Todo t WHERE t.user=:user")
        int countByUser(@Param("user") User user);



}
