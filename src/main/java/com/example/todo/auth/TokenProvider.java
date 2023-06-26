package com.example.todo.auth;

//0623
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import io.jsonwebtoken.Claims;
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
        claims.put("role", userEntity.getRole().toString());







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
                .setClaims(claims) //추가클레임은 먼저 설정해야함! //0626
                .setIssuer("딸기겅듀") //발급자가누구니   iss:발급자 정보
                .setIssuedAt(new Date()) //iat: 발급시간
                .setExpiration(expiry)  //만료시간(exp)은 위에서 만들었었다.
                .setSubject(userEntity.getId()) //sub: 토큰을 식별할 수 있는 주요 데이터. -> 유저의 식별자인 id를 넣어주면되겠지.
                //여기까지가 JWT를 사용하면 기본으로 들어갈 값.


                //추가 클레임 정의했던거
                //.setClaims(claims) 이거 맨위로올리자. 0626
                .compact();
    }




    //0626
    /**
     * 얘는 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조 여부를 확인할 것이고,
     * 토큰을 json으로 파싱(변환)해서 클레임(토큰 정보)를 리턴을 해 줄것이다.
     * @param token
     * @return -> 토큰 안에 있는 인증된 유저 정보를 반환.
     */
    public TokenUserInfo validateAndGetToKenUserInfo(String token){

        Claims claims = Jwts.parserBuilder() //토큰은 빌더를 사용했지만, 파싱(변환)한다했으니 메서드가 다름, 괄호 안에는 토큰 발급자의 발급 당시의 서명을 넣어주자.
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) //메서드임. 시크릿키를 바이트로변환.
                //서명이 위조 되었는지 먼저 검사함. 위조 된 경우에는 예외(에러)가 발생한다.
                //위조가 되지 않은 경우는 페이로드를 리턴한다.
                .build()
                //데이터꺼내자
                .parseClaimsJws(token)
                .getBody();//리턴이 클레임스이라는 타입의 리턴값이된다.

                log.info("claims: {}", claims); //클레임스는 참고로 위의 setClaims,딸기겅듀,new Date, expiry, setSubect 등을 가지고 있다!


                return TokenUserInfo.builder() //id,email,role
                        .userId(claims.getSubject()) //위에보면 setSubject로 id를 넣었으니. 서브젝트로 가져와야지?
                        .email(claims.get("email", String.class))
                        .role(Role.valueOf(claims.get("role", String.class))) //문자열로 넣었으니 문자열로 받아서 그것을 role타입으로 바꿔서 role한테주겠다.
                        .build();
    }






}
