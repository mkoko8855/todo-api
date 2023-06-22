package com.example.todo.userapi.dto;

import com.example.todo.userapi.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;




@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpResponseDTO {

    private String email;

    private String userName;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDate;





    public UserSignUpResponseDTO(User user){
        this.email = user.getEmail(); //유저에서 이메일꺼낼꺼고
        this.userName = user.getUserName();
        this.joinDate = user.getJoinDate();
    }


}
