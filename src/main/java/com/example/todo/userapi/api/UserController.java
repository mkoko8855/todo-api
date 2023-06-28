package com.example.todo.userapi.api;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

//0622 0623
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;


    //이메일 중복 확인 요청 처리
    //GET: /api/auth/check?email=zzzz@xxx.com
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
        if (email.trim().equals("")) { //매개값으로 넘어온 저 email에 공백제거다해. 그것이 같애. 빈거랑.
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }
        boolean resultFlag = userService.isDuplicate(email); //중복이 됐냐 안됐냐?
        log.info("{} 중복?? - {}", email, resultFlag);

        return ResponseEntity.ok().body(resultFlag); //클라이언트로 넘기기.

    }


    //회원 가입 요청 처리
    //POST: /api/auth
    @PostMapping
    public ResponseEntity<?> signup(@Validated @RequestBody UserRequestSignUpDTO dto, BindingResult result) { //signup은 회원가입. in은 로그인
        log.info("/api/auth POST - {}", dto);

        if (result.hasErrors()) {
            log.warn(result.toString()); //그냥 결과한번 찍어보고~
            return ResponseEntity.badRequest()
                    .body(result.getFieldError()); //방금 발생한 에러를 꺼내서 리턴해주자.
        }

        //문제없으면 create부르자!
        try {
            UserSignUpResponseDTO responseDTO = userService.create(dto);//서비스의 create를 부르면서 dto전달해주자. 그리고 지역변수로 삽입
            return ResponseEntity.ok()
                    .body(responseDTO);
        } catch (NoRegisteredArgumentsException e) {
            log.warn("필수 가입 정보를 전달받지 못했습니다.");
            return ResponseEntity.badRequest() //badRequest말고 status도 할 수 있다. 그냥 badRequest쓰자.
                    .body(e.getMessage());
        } catch (DuplicatedEmailException e) {
            log.warn("이메일이 중복되었습니다.");
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }


    //로그인 요청 처리
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Validated @RequestBody LoginRequestDTO dto) { //로그인할때 제이슨 형태로 정보가 날아오겠지

        try {
            LoginResponseDTO responseDTO = userService.authenticate(dto);
            return ResponseEntity.ok().body(responseDTO); //클라이언트로 전송.
        } catch (Exception e) {
            //에러날곳은 authenticate이잖아. 한번보자~
            e.printStackTrace();
            return ResponseEntity.badRequest() //배드리퀘스트가 맞지. 이메일이 없다는건 이메일을잘못적은거고 비번도 마찬가지고.
                    .body(e.getMessage());
        }
    }



    //회원가입하면 common등급은 기본으로 주는데 프리미엄으로 승격할수도있지? 0627
    //즉, 일반 회원을 프리미엄 회원으로 승격하는 요청 처리(권한 검사도같이)
    //crud 중, 수정이겠지. UPDATE작성임.

    //수정이니 풋or패치 맵핑
    @PutMapping("/promote") //로그인을 해야만함
    @PreAuthorize("hasRole('ROLE_COMMON')") //얘는, 권한 검사. 즉, 해당 권한이 아니라면 인가처리 거부하고 403 코드를 리턴한다. 0627 -> ROLE_COMMON이 아닌 애들은 다 내친다. 굳이 서비스에서 COMMON이 맞냐? 라고 확인안해도됨. ROLE_ 에 대해선 웹시큐리티클래스에 써놨음.
    public ResponseEntity<?> promote(@AuthenticationPrincipal TokenUserInfo userInfo){
        log.info("/api/auth/promote - PUT!");

        try{
           LoginResponseDTO responseDTO = userService.promoteToPremium(userInfo);
            return ResponseEntity.ok()
                    .body(responseDTO);
        }catch (IllegalStateException | NoRegisteredArgumentsException e){
            e.printStackTrace();
            log.warn(e.getMessage());
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }


    }








}