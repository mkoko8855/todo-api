package com.example.todo.userapi.entity;


import lombok.*;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid") //uuid를 랜덤으로 생성해서 식별자 만드는 전략. 즉, 아래 id는 계정 명이 아니라 식별 코드로 사용할 예정이다.
    private String id; //계정 명이 아니라 식별 코드로 사용할 것이다. 요즘엔 이메일을 계정으로 사용하는 경우도 많다. 회원을 구분하는 고유한 랜덤 문자열로, 유효식별코드로 사용하자.

    @Column(unique = true, nullable = false) //PK로 알수도있겠지만, PK는 @Id로준다. 이메일을 계정 명으로 쓸꺼면 고유해야 하니 유니크줬다. 이메일로 로그인할거다.
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;


    @Enumerated(EnumType.STRING)
    //@ColumnDefault("'COMMON'")
    @Builder.Default //기본값은 내가 지정한다! -> Role.COMMON;
    private Role role = Role.COMMON; //유저 권한. 직접초기화.



    //등급 수정 메서드
    public void changeRole(Role role){
        this.role = role;
    }



}
