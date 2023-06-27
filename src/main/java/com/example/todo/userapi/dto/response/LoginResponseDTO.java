package com.example.todo.userapi.dto.response;

import com.example.todo.userapi.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

//0623
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO { //로그인 성공 후, 클라이언트에게 전송할 데이터 객체



    //로그인 성공 후, 유저의 정보를 전달해달라는 요구설계(사항)이 있겠지.
    //그걸 유저엔터티에서 뽑아서 컨트롤러한테 주면됨.
    private String email;

    private String userName;

    @JsonFormat(pattern = "yyyy년 MM월 dd일")
    private LocalDate joinDate;

    private String token; //인증 토큰

    //private String message; //로그인 성공 후 띄울 메세지

    private String role; //권한  0627





    public LoginResponseDTO(User user, String token) { //얜 유저받고 토큰받으면 아래처럼 세팅해라!

        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.joinDate = LocalDate.from(user.getJoinDate()); //Localdate객체가 LocalDateTime으로바뀜!
        this.token = token;
        this.role = String.valueOf(user.getRole()); //스트링밸류오브는 어떤 타입이던 문자열로 변경해준다. 0627

    }
}
