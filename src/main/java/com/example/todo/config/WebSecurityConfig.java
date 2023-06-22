package com.example.todo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//0622
//@Configuration //설정 클래스 용도로 사용하도록 스프링에 등록하는 아노테이션.
@EnableWebSecurity //그러나 위에꺼 컨피그레이션 주석하고 이걸로쓰자. -> 즉, 시큐리티 설정 파일로 사용할 클래스 선언.
public class WebSecurityConfig { //유저서비스의 비밀번호 인코더(다른 빈 등록할때도 사용할거임)를 선언한 것을 사용하기 위해 sts의 xml부분을 클래스화시켰음.


    @Bean //외부 라이브러리 클래스를 스프링 컨테이너에 등록하고싶을 때 등록, 내가 만든클래스는 @컴포넌트 이런거붙이는데, 외부는 이렇게써야됨
    public PasswordEncoder passwordEncoder() {  //PasswordEncoder는 메서드이다.

        //메서드 등록을 위해
        return new BCryptPasswordEncoder();  //BCryptPasswordEncoder 이것도 메서드이다.
    }


    //시큐리티 설정 -> 리액트서버랑 jpa를 연동시키려하는데, 크로스오리진으로 설정해놔도, 이거 안해줘서 막는것이다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{ //매개변수로는, httpSecurity의 http로했다.. 우리가 부르는게아님~

        //시큐리티 모듈이 기본적으로 제공하는 보안 정책을 해제.
        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable();
        return http.build();
    }



}
