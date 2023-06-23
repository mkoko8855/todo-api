package com.example.todo.userapi.service;


import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//0622 0623
@Service
@Slf4j
@RequiredArgsConstructor //final안쓰고 오토와이어드안써도됨. 여러개 변수선언할거면 이거쓰자.
public class UserService {

    private final UserRepository userRepository;

    //비밀번호 암호화를 위한 변수선언 -> 이용하기 위해 빈등록해야됨. -> config패키지만들고 WebSecurityConfig클래스 만들었음.
    private final PasswordEncoder encoder;



    //회원 가입 처리
    public UserSignUpResponseDTO create(final UserRequestSignUpDTO dto) throws RuntimeException { //컨트롤러가 얘를 부르겠지. dto를받아서 서비스로넘기면 서비스가 화면단으로 넘기겠지. UserRequestSignUpDTO 는 일단 임의로 만들었음. 이건, 유저 회원가입 요청(유저엔터티쪽에서)이 들어올때, 그때 사용할 dto다.

        //throws를 사용하는 이유는 예외가 다양하게 발생할떄, 부르는 곳에서 처리하기 위해 쓴다.
        //create메서드에서 발생하는 아래 2가지 예외(가입정보X, 중복된이메일)를 create 메서드를 부르는 컨트롤러가 처리할 수 있다.
        //그러나 아래 2가지 예외도 둘다 타입이 런타임익셉션이다. 컨트롤러는 둘다 구분을 못함.
        //각각 익셉션을 디자인해야한다. exception패키지를만들자.


        String email = dto.getEmail(); //dto.getEmail자꾸 선언해줘야하니 변수로 줘버리자.
        if(dto == null || email.equals("")){ //널이거나 공백이면
            throw new NoRegisteredArgumentsException("가입 정보가 없습니다. ");
        }

        if(userRepository.existsByEmail(email)){
            //만약, dto에서 이메일을 꺼내서 전달할 때, 만약 트루가떴다? 그럼 이미 이메일이 존재하는거니까
            log.warn("이메일이 중복되었습니다. - {}", email);
            throw new DuplicatedEmailException("중복된 이메일 입니다.");
        }


        //패스워드 인코딩
        String encoded = encoder.encode(dto.getPassword()); //dto.getPassword는 날것데이터임.
        dto.setPassword(encoded);


        //중복확인했으니, 데이터넣으려면 유저엔터티로해야겠지(지금 우리는 dto를가지고있으니 변환)
        User user = dto.toEntity();
        User saved = userRepository.save(user); //User saved안써도됨. 아래 알림창띄울라고 User saved해줬음


        //알림창이나띄워주자
        log.info("회원 가입 정상 수행됨! - saved user -{}", saved); //알림창띄워보려고 위에 User saved를 변수로선언했음.


        //이메일 이름, 가입된 시간 등을 리턴해주기 위해 객체를 하나 디자인할꺼다.
        return new UserSignUpResponseDTO(saved);

    }


    public boolean isDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}
