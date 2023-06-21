package com.example.todo.todoapi.dto.response;


import com.example.todo.todoapi.entity.Todo;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TodoDetailResponseDTO { //조회했을 때 내용. -> 클라이언트에게 전달하는 용도로 사용하는 DTO가되겠다.
//0621
    private String id;

    private String title;

    private boolean done;

    //엔터티를 DTO로 만들어주는 생성자(알트+인서트)
    public TodoDetailResponseDTO(Todo todo) { //매개값으론 엔터티받자 -> 투두를 전달해서 dto로바꾸자
        this.id = todo.getTodoId();
        this.title = todo.getTitle();
        this.done = todo.isDone();   //불린타입은 getter메서드가 isDone();이다.



    }
}
