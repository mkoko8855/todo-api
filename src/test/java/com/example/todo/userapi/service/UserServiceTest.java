package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@Transactional
@Rollback(false)
class UserServiceTest { 

    @Autowired
    UserService userService;

    
    @Test
    @DisplayName("중복된 이메일로 회원 가입을 시도하면, RuntimeException에러가 발생해야 한다.")
    void vailidateEmailTest() { //중복된이메일을 하면 에러나도록 세팅했잖아 -> userService에서, if(userRepository.existsByEmail(email)){ 부분의 "중복된 이메일 입니다." 부분! 이걸 테스트하겠다는거임.
        //given
        String email = "abc1234@naver.com";

        //then절에서 적을 dto를위해
        UserRequestSignUpDTO dto = UserRequestSignUpDTO.builder()
                .email(email)
                .password("asdf")
                .userName("qwer")
                .build();


        //when
        //회원가입요청을보내면, (예외터지는걸 단언할껀데, 어썰트를 통해 예외를 받는방법이있다.)


        //then (예외터지는걸 단언할껀데, 어썰트를 통해 예외를 받는방법이있다.)    유저서비스의 create문을 부르면서, 나는 단언한다)
        assertThrows(RuntimeException.class, () -> {
            userService.create(dto, "");
        }); //첫번째는 어떤 에러가 발생할 지의 에러의 클래스, 두번째매개값으로는 에러발생상황을 함수형식으로 선언.
    }
    
    
    
    
    
    
    
}