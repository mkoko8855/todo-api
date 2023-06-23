package com.example.todo.userapi.api;

import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> check(String email){
        if(email.trim().equals("")){ //매개값으로 넘어온 저 email에 공백제거다해. 그것이 같애. 빈거랑.
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
    public ResponseEntity<?> signup(@Validated @RequestBody UserRequestSignUpDTO dto, BindingResult result){ //signup은 회원가입. in은 로그인
        log.info("/api/auth POST - {}", dto);

        if(result.hasErrors()){
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
        } catch (DuplicatedEmailException e){
            log.warn("이메일이 중복되었습니다.");
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }



    }




}
