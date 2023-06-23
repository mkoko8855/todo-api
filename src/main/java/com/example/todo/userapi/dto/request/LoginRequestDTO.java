package com.example.todo.userapi.dto.request;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
//이번에는 @Setter구축X -> 로그인 요청과 함께 넘어온 데이터를 내가 굳이 setter로 변경을할일이있나. 없으니 세터작성X
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;



}
