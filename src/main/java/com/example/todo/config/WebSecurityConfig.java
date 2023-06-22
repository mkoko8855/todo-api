package com.example.todo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //설정 클래스 용도로 사용하도록 스프링에 등록하는 아노테이션.
public class WebSecurityConfig { //유저서비스의 비밀번호 인코더(다른 빈 등록할때도 사용할거임)를 선언한 것을 사용하기 위해 sts의 xml부분을 클래스화시켰음.


    @Bean //외부 라이브러리 클래스를 스프링 컨테이너에 등록하고싶을 때 등록, 내가 만든클래스는 @컴포넌트 이런거붙이는데, 외부는 이렇게써야됨
    public PasswordEncoder passwordEncoder() {  //PasswordEncoder는 메서드이다.

        //메서드 등록을 위해
        return new BCryptPasswordEncoder();  //BCryptPasswordEncoder 이것도 메서드이다.
    }




}
