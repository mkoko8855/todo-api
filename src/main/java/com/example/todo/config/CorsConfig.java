package com.example.todo.config;

//0623

//전역 크로스 오리진 설정

import org.hibernate.event.internal.EntityCopyAllowedObserver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //설정파일이기때문에 이거써주자.
public class CorsConfig implements WebMvcConfigurer { //인터페이스 구현. 웹엠뷔씨컨피규어러는 메서드다.


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/**") //어떤 요청에 대해 허용을 할지 써주면된다.
                .allowedOrigins("http://localhost:3000") //어떤 클라이언트를 허용할지 써주자.
                .allowedMethods("*") //요청 방식을 무엇으로만 허용할지. 여러개쓸수있다. 겟과 포스트방식을 허용한다던지, 아니면 다좋으면 *만찍으면된다.
                .allowedHeaders("*") //어떤 요청 헤더를 허용할지
                .allowCredentials(true) //쿠키 전달을 허용할 것인지
                .maxAge(3600);//초단위로주면됨. 캐싱 시간을 설정-> 요청이 들어올때 이전과 같은 요청이 또들어오면 서버요청으로 처리하지않고 메모리에 남겨두고 메모리에 남겨놓고 그걸로 응답하는 방식.
    }

}
