package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false) //그냥 연습이니 false로..
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    
    @Test
    @DisplayName("회원 가입 테스트")
    void saveTest() {
        //given
        User newUser = User.builder()
                .email("abc1234@naver.com")
                .password("1234")
                .userName("춘식이")
                .build();
        //when
        User saved = userRepository.save(newUser);
        //then
        assertNotNull(saved);

    }
    

    
    @Test
    @DisplayName("이메일로 회원 조회하기")
    void findEmailTest() {
        //given
        String email = "abc1234@naver.com";
        //when
        Optional<User> userOptional = userRepository.findByEmail(email);
        //then
        assertTrue(userOptional.isPresent()); //단언메서드1번째
        //옵셔널 정보얻어볼까
        User user = userOptional.get(); //객체받았고,
        assertEquals("춘식이", user.getUserName()); //객체 받는지, 다시 검증. 즉, 단언메서드2번째

        System.out.println("\n\n\n");
        System.out.println("user = " + user);
        System.out.println("\n\n\n");
    }
    
    
    
    @Test
    @DisplayName("이메일 중복체크를 하면 중복값이 false여야 한다.")
    void emailFalse() {
        //given
        String email = "db1234@naver.com";
        //when
        boolean flag = userRepository.existsByEmail(email); //없으니 false가있다고 단언하자.
        //then
        assertFalse(flag);

    }

}