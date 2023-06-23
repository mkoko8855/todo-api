package com.example.todo.todoapi.api;


import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
//0622 0623
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos") //공통url맵핑
//@CrossOrigin(origins = "http://localhost:3000") //여러개의 url지정이면 배열형태로줘도됨. 우린 하나니까 문자열형태로 하나만.
public class TodoController {

    //얜 서비스와 의존관계있으니
    private final TodoService todoService;


    //할 일 등록 요청
    @PostMapping
    public ResponseEntity<?> createTodo(@Validated @RequestBody TodoCreateRequestDTO requestDTO, BindingResult result) {
        log.info("add 요청 들어옴");
        if (result.hasErrors()) { //에러있어?
            log.warn("DTO 검증 에러가 발생했어 그 에러는 : {}", result.getFieldError()); //여러개검증은 Errors로 ForEach써도됨. TodoCreateRequestDTO가보면 title 하나만했으니, Error만적어도됨..
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }
        //입력값 검증 통과하면 if문 건너뛰니..이제부르자
        //todoService.create(requestDTO); 그러나 이거 트라이캐치로 쓸 것임.
        try {
            TodoListResponseDTO responseDTO = todoService.create(requestDTO); //requestDTO는 위에 매개값으로적은것
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder().error(e.getMessage()));
        }

    }


    //할 일 삭제 요청
    @DeleteMapping("/{id}") //id를받음
    public ResponseEntity<?> deleteTodo(@PathVariable("id") String todoId) { //매개값으로 경로에 묻어있는 id를 받아오기 위해, 패쓰베리어블 작성하자. uuid니까 String으로.
        log.info("/api/todos/{} DELETE request요청이왔어요!", todoId);

        //id가 이상한게 왔을 수도 있으니, 검증하자
        if (todoId == null || todoId.trim().equals("")) { //todoId가 널이거나 혹은 공백을 제거한 결과가 빈 문자열이라면, 삭제진행할수없지.

            return ResponseEntity.badRequest()
                    .body(TodoListResponseDTO.builder().error("ID를 전달 해 주세요"));

        }


        try {
            TodoListResponseDTO responseDTO = todoService.delete(todoId); //서비스야. 삭제해줘. todoId줄게. delete 메서드를 컨트롤러에서 만들러가자.
            return ResponseEntity.ok().body(responseDTO); //responseDTO라는 최신의 정보를 화면단으로 보낸다.
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(TodoListResponseDTO.builder().error(e.getMessage()));
        }


    }


    //할 일 목록 요청
    @GetMapping
    public ResponseEntity<?> retrieveTodoList() {
        //리트라이브 불러서 보내주면 끝..
        log.info("/api/todos GET request");
        TodoListResponseDTO responseDTO = todoService.retrieve();

        return ResponseEntity.ok().body(responseDTO);

    }


    //할 일 수정 요청(풋과 패치를 같이썻음)
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<?> updateTodo(@Validated @RequestBody TodoModifyRequestDTO requestDTO, BindingResult result, HttpServletRequest request) { //풋인지 패치인지 구분하고싶으면 HttpServlet써도됨~

        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(result.getFieldError());
        }

        //HttpServlet쓴거로 확인하자
        log.info("/api/todos {} request!", request.getMethod()); //무슨요청으로 들어왔는지 확인할 수 있음
        //데이터도 한번 확인해볼까
        log.info("modifying dto: {}", requestDTO);

        try {
            TodoListResponseDTO responseDTO = todoService.update(requestDTO);
            return ResponseEntity.ok().body(responseDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(TodoListResponseDTO.builder().error(e.getMessage()));
        }

    }
}
