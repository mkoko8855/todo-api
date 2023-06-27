package com.example.todo.filter;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//0626
//클라이언트가 전송한 토큰을 검사하는 필터(로그인 했을 때 회원이 아닌지 맞는지 검사해주는 필터)
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { //JWT를 검증할 수 있는 필터. OncePerRequestfilter는 메서드다. 요청마다 한번씩 해야됨.


    private final TokenProvider tokenProvider; //토큰을제공하고 유효성검사 해주는 애.




    //필터가 해야 할 작업을 기술하자.
    @Override
    protected void doFilterInternal(HttpServletRequest request, //이건, 요청정보를 가지고있는 request이다. get이냐 포스트냐..아이템은뭐냐..파라미터는뭐냐..세션 등..여러가지꺼낼수있음.
                                    HttpServletResponse response, //이건, 응답정보를 담아서 전달할 때 레스폰스를 사용하게 된다.
                                    FilterChain filterChain) throws ServletException, IOException {


        try {
            String token = parseBearerToken(request);
            log.info("Jwt Token Filter is running...- token: {}", token);


            //토큰 위조검사 및 인증 완료 처리
            //내가만든 토큰인지 확인해봐야하니까.
            if(token != null){
                //토큰 서명 위조 검사와 토큰을 파싱해서 클레임을 얻어내는 작업을 진행하자.
                //암호화된 정보는 야믈가면 jwt: secret으로 서명을 해놨다. 이 값은 토큰프로바이저클래스가 들고있음. 쟤가 할일이니까.
                //즉, 토큰프로바이저 메서드를 부르고, 우리가 얻은 토큰을 보내주자.
                TokenUserInfo userInfo = tokenProvider.validateAndGetToKenUserInfo(token);


                //인가 정보 리스트
                List<SimpleGrantedAuthority> authorityList
                        = new ArrayList<>();
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + userInfo.getRole().toString())); //매개값으로는 role은 userInfo에 들어있으니 -> 0627


                //유저인포까지 얻어왔으니, 인증완료처리진행하자.
                //-> 스프링 시큐리티에게 인증정보를 전달해서 API안에서 전역적으로 활용할 수 있도록 하자.(즉, 전역적으로 인증정보를 활용할 수 있게 설정할 것이다)
                AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userInfo, //컨트롤러에서 활용할 유저 정보.
                    null,    //두번째매개값으로는 비번을 주는데, null로 고정하자. 즉, 인증된 사용자의 비밀번호를 전달하는데 보통은 널값준다. 토큰에 비번을 넣는건 위험하니까
                    authorityList //세번째 값으로는 인가 정보. 즉, 권한 정보를 추가로 줄 수가 있다. 즉, 어느 정도에 권한을 허용할지. 타입이 리스트다. 위에다가 선언할 것이다.
                );

                //인증 완료 처리시 클라이언트의 요청 정보 세팅
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //그리고 스프링 시큐리티 컨테이너에 인증 정보 객체를 등록
                SecurityContextHolder.getContext().setAuthentication(auth);

            }

        } catch (Exception e) {
            e.printStackTrace(); //로그확인위해 프린트스택~
            log.error("토큰이 위조가 되었거나 만료 되었습니다.");
        }

        //필터 체인에 내가 만든 필터 실행 명령.  이거 안해주면 넘어가지가 않음.
        filterChain.doFilter(request, response);

    }

    private String parseBearerToken(HttpServletRequest request) { //requst객체를 통해 헤더를꺼낼꺼임.

        //이제 필터 안에서,
        //요청 헤더에서 토큰을 가져오자.
        //클라이언트쪽에서 토큰을 헤더에 담아서 보낼꺼니까. 그 토큰값을 가지고 올 거다.
        //content-type : application/json이거나, 그리고 Authorization : Bearer asda%&^sdac2as@(즉, 토큰값) 이런게 전부 요청헤더의 정보다.

        //헤더에 접근하는 방법은?
        //위의 HttpServletRequest request 에다가 달라해. 요청정보가지고있으니
        String bearerToken = request.getHeader("Authorization");//getHeader라는 메서드가 있다.

        //요청 헤더에서 가져온 토큰은 순수 토큰 값이 아닌,
        //앞에 Bearer가 붙어있으니, 이것을 제거하는 작업을 진행하자.
        if(StringUtils.hasText(bearerToken) //Stringutils는 스프링에서 제공하는 객체이다. 얘를 이용하면 쉽다.
        && bearerToken.startsWith("Bearer")) {  //배어러토큰이 Bearer로 시작한다면,

            return bearerToken.substring(7); //7번인덱스부터끝까지잘라라.


        }
        //만약에 없으면(if문통과못하면)
        return null;
    }

}
