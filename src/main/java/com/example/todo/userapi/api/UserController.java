package com.example.todo.userapi.api;

import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//0622
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
        boolean resultFlag = userService.isDuplicate(email);
        log.info("{} 중복?? - {}", email, resultFlag);

        return ResponseEntity.ok().body(resultFlag);

    }
    



    //회원 가입 요청 처리




}
