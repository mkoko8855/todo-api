package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional //JPA는 트랜잭션 단위로 동작하니, 이거 꼭 넣어주자.
public class TodoService {

    //서비스는 레파지토리랑 의존관계가있으니,
    private final TodoRepository todoRepository; //오토와이어드 말고 final할꺼면, @RequiredArgsConstructor로하자.

    private final UserRepository userRepository; //0626


    //할 일 목록 조회
    //요청에 따라 데이터 갱신, 삭제 등이 발생 한 후에
    //최신의 데이터 내용을 클라이언트에게 전달해서 렌더링 하기 위해
    //목록 리턴 메서드를 서비스에서 처리하려고 이 메서드를 쓴다.
    public TodoListResponseDTO retrieve(String userId){ //갱신, 수정, 삭제 될 때마다 이 retrieve를 불러야함.


        //로그인 한 유저의 정보를 DB에서 조회. 0626
        User user = getUser(userId);
        //위 메서드는
        /*
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        원래 이거였는데, 많이 쓰일 것 같아서 메서드화시킴.
        */




        List<Todo> entityList = todoRepository.findAllByUser(user); //레파지토리야. 다찾아와. 0626


        //List<TodoDetailResponseDTO> dtoList = new ArrayList<>();
        //for (Todo todo : entityList) { //todo변수에 하나씩 전달될때마다 객체 생성
        //    TodoDetailResponseDTO dto = new TodoDetailResponseDTO(todo);
        //    dtoList.add(dto);
        //이거를 모던하게 바꾸자.


        //모던하게
        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                .map(todo -> new TodoDetailResponseDTO(todo))
                        .collect(Collectors.toList()); //새로운 리스트로 다시 리턴. 리턴한 결과가 위 dtoList 이걸로 들어간다.

        return TodoListResponseDTO.builder() //리턴한 결과가 dtoList 이걸로 들어간다.
                .todos(dtoList)
                .build();


        }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }


    //할 일 삭제(컨트롤러에 의한 delete 메서드)
    public TodoListResponseDTO delete(final String todoId, String userId) { //파이널 붙이면, 서비스단에서 id를 참조할 순 있지만 수정할 순 없게끔 할 수 있다.

        try {
            todoRepository.deleteById(todoId);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, error: {}", todoId, e.getMessage());
            throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
        }

        return retrieve(userId); //delete작업하고 ,retrieve호출하고 위 TodoListResponseDTO 타입으로 받겠다. 그걸 다시 컨트롤러로 리턴하겠지.

    }



    //할 일 등록요청 create 메서드문
    public TodoListResponseDTO create(final TodoCreateRequestDTO requestDTO, final String userId)throws RuntimeException{

        Todo todo = requestDTO.toEntity(getUser(userId));

        todoRepository.save(todo); //전달받은 requestDTO를 엔터티로 바꿔서 보내줘야한다. 바꾼건 해놨잖아. TodoCreateRequestDTO에.
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());
        return retrieve(userId);
    }



    //할 일 수정요청 update 메서드문
    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO, String userId)throws RuntimeException {

      Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId()); //이러면 옵셔널이온다. orElseThrow써줄 수 있지만, 오랜만에 다르게 써보자.

        targetEntity.ifPresent(entity -> { //조회 결과가 제대로 존재 한다면? 클라이언트에서 done 값을 세팅하고 그것을 save할 것이다. 아래다 이 순서대로 로직적자.
            entity.setDone(requestDTO.isDone()); //셋 던의 값은 뭘로변경해? -> 클라이언트가 전달한 Done값을 그냥 꽂아넣어. 리액트에서 이미 뒤집어서 굳이 내가 뒤집을 필요 X

            todoRepository.save(entity); //조회 후 변경 후 save 다시 하면, JPA가 상태변경을 감지하고 update문을 떄려준다고했었다~

        });

        //변경 완료된 가장 최신의 정보를 가져와야 하기에
        return retrieve(userId);


    }
}
