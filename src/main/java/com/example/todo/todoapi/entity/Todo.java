package com.example.todo.todoapi.entity;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "todoId") //todoId가 같으면 같은 객체로 인식 시키겠다.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_todo")
public class Todo {

    @Id
    @GeneratedValue(generator = "system-uuid")   //uuid로 진행하니, 제네릭제너레이터가 필요함
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String todoId;



    @Column(nullable = false, length = 30) //길이 지정안하면 기본이 255이다.
    private String title; //할일의 정보(제목)


    private boolean done; //할일 완료 여부. 자바스크립트의 배열,객체타입으로 선언했었는데 이젠 DB로 관리할꺼다.
    

    @CreationTimestamp
    private LocalDateTime createDate; //등록 시간


}
