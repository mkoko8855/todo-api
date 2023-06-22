package com.example.todo.userapi.dto.request;


import com.example.todo.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "email") //이메일만 같으면 같은객체로인식.
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestSignUpDTO { //(지금 엔터티는 User이다. id랑 joinDate가빠졌다. 굳이 필요없다.)




    //id는 굳이 안적어도됨. 여러가지가들어오겠지. 이름부터적어보자.
    @NotBlank
    @Size(min = 2, max = 5)
    private String userName;




    @NotBlank
    @Email //이메일형식인지를검증해주는것.
    private String email;




    @NotBlank
    @Size(min = 8, max = 20)
    private String password;


    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .userName(this.userName)
                .build();
    }





}
