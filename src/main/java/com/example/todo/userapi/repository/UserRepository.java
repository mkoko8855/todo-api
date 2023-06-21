package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {


    //쿼리 메서드

    //즉, 이메일로 회원 정보를 조회하자.
    Optional<User> findByEmail(String email); //리턴은 유저객체로 받아도 되는데, 옵셔널로받자. 그럼 널체크도 쉽겠지.

    //이메일 중복 체크(이메일이 DB에 몇개오는지 조회하는, 그게 중복이 됐는지아닌지 볼 수 있다. 0이면 중복아니고 1이면 중복이고..제약조건에 유니크걸어서 중복된 이메일은 애초에 불가능.
    //@Query("SELECT COUNT(*) FROM User u WHERE u.email = ?1") //네이티브가 아닌 JPQL로썼다. 그러나 모르면 이렇게 쿼리때리라는거고, 아래가 편한 방법이다.
    boolean existsByEmail(String email);


}
