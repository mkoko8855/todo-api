package com.example.todo.exception;

//0623
import lombok.NoArgsConstructor;

@NoArgsConstructor //기본생성자 아노테이션이며, 매개값을 안받는거.
public class NoRegisteredArgumentsException extends RuntimeException{ //RuntimeException, InputMisMatched도있고, 어떤 예외랑 비교해서 결이 다르다 싶으면, 즉 어울리는 예외타입없다싶으면 그냥 Exception 써도됨. 모르겠다 싶으면 Exception쓰자.

    //여기서  기본 생성자는 필수고, 에러 메세지를 받는 생성자도 필요하다. 롬복사용하자.

    
    //기본생성자
    public NoRegisteredArgumentsException(String message) {
        super(message);
    }
}
