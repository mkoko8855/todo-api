package com.example.todo.todoapi.dto.response;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TodoListResponseDTO { //목록 요청이 들어왔을 때, 응답해야할클래스

    private List<TodoDetailResponseDTO> todos; //TodoDetailResponseDTO는 하나의 객체고, 전체 목록을 요청했을 떄 전달 해주기 위해 List로 선언.

    //뭔가 더 보내줄 데이터가 있다면?
    private String error; //혹시라도 에러가 발생하면, 에러 메세지를 담을 필드를 추가로 선언했다.



}
