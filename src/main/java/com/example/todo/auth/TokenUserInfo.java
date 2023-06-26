package com.example.todo.auth;


import com.example.todo.userapi.entity.Role;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {
    
    //필요한 값만 적어보자
    private String userId; 

    private String email;

    private Role role;



}
