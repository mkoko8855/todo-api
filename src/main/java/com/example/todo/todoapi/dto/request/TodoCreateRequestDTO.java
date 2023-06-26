package com.example.todo.todoapi.dto.request;

import com.example.todo.todoapi.entity.Todo;
import com.example.todo.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

//todo가 실행(create할) 요구를 할 dto들의 모음.
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoCreateRequestDTO {  //얜 테이블이 아니다. 즉, 엔터티아니다..@Entity하면안됨.

    @NotBlank //스트링이니 낫블랭크줘도되겠지
    @Size(min = 2, max = 10)
    private String title;


    /*
dto패키지 안에 request만들고 TodoCreateRequestDTO 클래스를 만들었다.
이 클래스는 사용자가 할 일을 등록하기 위해 뭔가 작성하고, 엔터버튼을 누르면 요청이 서버로오겠지? 그걸 받는 것이다.
받을땐, 엔터티로 변환해야겠지.
바로 변환하자.  -> dto를 엔터티로 변환  */



    public Todo toEntity(){
        return Todo.builder()
                .title(this.title)
                .build();
    } //그냥 단순히 오버로딩부분. 마치 기본생성자처럼..


    //dto를 엔터티로 변환
    public Todo toEntity(User user){
        return Todo.builder()
                .title(this.title)
                .user(user)
                .build();
    }

}
