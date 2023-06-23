package com.example.todo.auth;

//0623
import com.example.todo.userapi.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.privilegedactions.SetContextClassLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//유저서비스가 토큰프로바이저를 의존한다.
@Component
@Slf4j
public class TokenProvider { //얘의 역할은, 토큰을 발급하고, 서명 위조를 검사하는 객체

    // 서명에 사용할 값으로 사용함. (512비트 이상의 랜덤 문자열로 지정할 것을 권장함.)
    @Value("${jwt.secret}")
    private String SECRET_KEY; //야믈가서 내용 값 작성.







    //토큰 생성 메서드

    /**
     * 이건 JSON WEB TOKEN을 생성하는 메서드이다.
     * @param userEntity -> 토큰의 내용(클레임)에 포함될 유저 정보 -> 너 로그인했네 누구야? -> 그 회원이 누구인지에 대한 토큰의 내용(정보)를 담기위한.
     * @return -> 생성된 JSON을 암호화한 토큰값.
     */
    public String createToken(User userEntity){ //메서드안에서 생성해서 문자열로 리턴해줄거임



                 /*
                    JWT는 어떤게 들어가느냐?
                    {
                        "iss": "춘식이",   서비스(발급자)이름
                        "exp": "2023-07-23", 토큰의 유효한 시간
                        "iat": "2023-06-23", 발급날짜
                        여기까지 기본으로들어감.


                        "email": "로그인한 사람 이메일",   ->내가만듬.
                        "role": "Premium"   //이사람은~프리미엄이야~ ->내가만듬.

                        //...더적을수있음..
                        //...더적을수있음..

                        //여기까지 적은 것들을 클레임이라고 한다.

                        == 서명
                    }
                 */




        //토큰 만료 시간 생성
        Date expiry = Date.from(
                Instant.now().plus(1, ChronoUnit.DAYS) //하루.
        );



         /*
        "email": "로그인한 사람 이메일",   ->내가만듬.
        "role": "Premium"   //이사람은~프리미엄이야~ ->내가만듬.
        이거 커스텀한거 정의해주자.
         */
        //추가 클레임 정의
        Map<String, Object> claims = new HashMap<>();
        //토큰에 집어넣고싶으면 map에다가 put해. map은 메서드가 add가아님.
        claims.put("email", userEntity.getEmail());
        //롤하나선언헀으니 롤도뽑을까
        claims.put("role", userEntity.getRole());







        //토큰 생성해서 리턴해주겠다.
        return Jwts.builder()
                //암호화해서 압축할꺼다.

                //메서드불러서 값집어넣자.
                //먼저, token header에 들어갈 서명이 들어가고, 서명은 signWith메서드를이용한다.
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), //문자열(시크릿키)을 바이트로 나열해서 전달한다.
                        SignatureAlgorithm.HS512 //알고리즘 방식이며, 우리가준비한 랜덤문자열을 바이트로 전달해서 한번 더 암호화를 진행한다. 외부에노출되면안되니.
                )
                //이번에는 token payload에 들어갈 클레임(토큰의 내용)을 설정한다.
                .setIssuer("딸기겅듀") //발급자가누구니   iss:발급자 정보
                .setIssuedAt(new Date()) //iat: 발급시간
                .setExpiration(expiry)  //만료시간(exp)은 위에서 만들었었다.
                .setSubject(userEntity.getId()) //sub: 토큰을 식별할 수 있는 주요 데이터. -> 유저의 식별자인 id를 넣어주면되겠지.
                //여기까지가 JWT를 사용하면 기본으로 들어갈 값.


                //추가 클레임 정의했던거
                .setClaims(claims)
                .compact();
    }
}
