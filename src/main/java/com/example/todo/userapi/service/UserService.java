package com.example.todo.userapi.service;


import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.aws.S3Service;
import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//0622 0623
@Service
@Slf4j
@RequiredArgsConstructor //final안쓰고 오토와이어드안써도됨. 여러개 변수선언할거면 이거쓰자.
public class UserService {

    private final UserRepository userRepository;

    //비밀번호 암호화를 위한 변수선언 -> 이용하기 위해 빈등록해야됨. -> config패키지만들고 WebSecurityConfig클래스 만들었음.
    private final PasswordEncoder encoder;


    private final TokenProvider tokenProvider; //TokenProvider클래스를 위한 주입. 주입하려면 빈등록해야지.


    private final S3Service s3Service; //0704




    //@Value("${upload.path}") //야믈에적었던거(업로드) 주입받기위해
    //private String uploadRootPath; //0628 -> 그러나 0704할때 필요없음.

    

    //회원 가입 처리
    public UserSignUpResponseDTO create(final UserRequestSignUpDTO dto, final String uploadedFilePath) throws RuntimeException { //컨트롤러가 얘를 부르겠지. dto를받아서 서비스로넘기면 서비스가 화면단으로 넘기겠지. UserRequestSignUpDTO 는 일단 임의로 만들었음. 이건, 유저 회원가입 요청(유저엔터티쪽에서)이 들어올때, 그때 사용할 dto다.

        //throws를 사용하는 이유는 예외가 다양하게 발생할떄, 부르는 곳에서 처리하기 위해 쓴다.
        //create메서드에서 발생하는 아래 2가지 예외(가입정보X, 중복된이메일)를 create 메서드를 부르는 컨트롤러가 처리할 수 있다.
        //그러나 아래 2가지 예외도 둘다 타입이 런타임익셉션이다. 컨트롤러는 둘다 구분을 못함.
        //각각 익셉션을 디자인해야한다. exception패키지를만들자.


        String email = dto.getEmail(); //dto.getEmail자꾸 선언해줘야하니 변수로 줘버리자.
        if(dto == null){ //널이거나 공백이면
            throw new NoRegisteredArgumentsException("가입 정보가 없습니다. ");
        }

        if(isDuplicate(email)){
            //만약, dto에서 이메일을 꺼내서 전달할 때, 만약 트루가떴다? 그럼 이미 이메일이 존재하는거니까
            log.warn("이메일이 중복되었습니다. - {}", email);
            throw new DuplicatedEmailException("중복된 이메일 입니다.");
        }


        //패스워드 인코딩
        String encoded = encoder.encode(dto.getPassword()); //dto.getPassword는 날것데이터임.
        dto.setPassword(encoded);


        //중복확인했으니, 데이터넣으려면 유저엔터티로해야겠지(지금 우리는 dto를가지고있으니 변환)
        User user = dto.toEntity(uploadedFilePath);
        User saved = userRepository.save(user); //User saved안써도됨. 아래 알림창띄울라고 User saved해줬음


        //알림창이나띄워주자
        log.info("회원 가입 정상 수행됨! - saved user -{}", saved); //알림창띄워보려고 위에 User saved를 변수로선언했음.


        //이메일 이름, 가입된 시간 등을 리턴해주기 위해 객체를 하나 디자인할꺼다.
        return new UserSignUpResponseDTO(saved);

    }


    public boolean isDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }





    //회원 인증 , 우리 회원인지 아닌지 결정해서 로그인시켜줄지 말지.
    public LoginResponseDTO authenticate(final LoginRequestDTO dto){ //클라이언트에서 데이터가 넘어오겠지 뭘로받을까, LoginRequestDTO dto 로 받자.

        //조회먼저해야겠지. 사용자가 로그인을 위해 보내준 정보랑 회원 정보랑 비교하려고.

        //이메일을 통해 회원 정보를 조회.
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow( //데이터가존재하지않으면 런타임익셉션을 실행함.
                        () -> new RuntimeException("가입된 회원이 아닙니다.")
                );

        //비밀번호 검증해야되는데, 암호화해놨잖아.
        //패스워드 검증
        String rawPassword = dto.getPassword(); //사용자가 입력한 날것의 비밀번호
        String encodedPassword = user.getPassword(); //DB에 저장된 비밀번호(암호화되어있음)

        if(!encoder.matches(rawPassword, encodedPassword)){ //일치하지않으면
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        //if문 나왔다는건, 비밀번호 정확히 입력한것.
        log.info("{}님 로그인 성공!", user.getUserName());

        //이 사람이 로그인했다는 징표를 남겨줘야지. 전에는 세션이나 쿠키로 했다. 토큰으로하자. (JWT)
        //즉, 로그인 성공 후에 클라이언트에게 뭘 리턴할 것인가?
        // -> JWT를 클라이언트에게 발급 해 줘야 함.
        String token = tokenProvider.createToken(user);
        
        return new LoginResponseDTO(user, token); //로그인한 사용자의 정보와 토큰을 주자 -> 얘는 컨트롤러부르잖아.


    }



    //0627
    //프리미엄으로 등급 업
    public LoginResponseDTO promoteToPremium(TokenUserInfo userInfo) throws NoRegisteredArgumentsException, IllegalStateException {

        //등급 업그레이드시킬 회원을 일단 조회.
        User foundUser = userRepository.findById(userInfo.getUserId())
                .orElseThrow(() -> new NoRegisteredArgumentsException("회원 조회에 실패!"));




    //일반 회원이 아니면 예외
    if (userInfo.getRole() != Role.COMMON){
        throw new IllegalStateException("일반 회원이 아니면 등급을 상승시킬 수 없습니다.");
    }

        //등급 변경
        foundUser.changeRole(Role.PREMIUM);
        User saved = userRepository.save(foundUser);

        //변경된 권한에 맞게 토큰을 재발급하자
        //발급은 프로바이저가해줬지
        //즉, 새로운 롤을 재발급해주자.
        String token = tokenProvider.createToken(saved);


    return new LoginResponseDTO(saved, token);


}



    /**  0628
     * 업로드된 파일을 서버에 저장하고 저장 경로를 리턴
     * @param originalFile - 업로드 된 파일의 정보
     * @return 실제로 저장된 이미지 경로
     */

     public String uploadProfileImage(MultipartFile originalFile) throws IOException {  //0628
         //야믈에서 만든 그 C:/파일을 작성해줌.
         //즉, 루트 디렉토리가 존재하는 지 확인 후 존재하지 않으면 생성

         //File rootDir = new File(uploadRootPath); 이거 주석처리해. 0704
         //if(!rootDir.exists()) rootDir.mkdir(); 이거 주석처리해. 0704 폴더가필요없으니

         //파일명을 유니크(고유)하게 변경하자.
         String uniqueFileName = UUID.randomUUID() + "_" + originalFile.getOriginalFilename(); //UUID에 랜덤UUID~ 원랜 tostring이지만, 오리지날 파일과 겹처서 작성한다.

         //파일명까지 유니크하게 생성했으니, 저장하자. -> 그러나 0704 AWS에 의해 주석처리해.
         //File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
         //originalFile.transferTo(uploadFile);

         //파일을 S3 버킷에 저장(0704)
         //야믈에 buketName적은거있지. 거기다 저장하겠다는 것이다.
         String uploadUrl
                 = s3Service.uploadToS3Bucket(originalFile.getBytes(), uniqueFileName);//매개변수로 멀티파일로했으니, 바꿔야겠지.


         //파일 경로를 리턴해야지
         return uploadUrl;

     }


     //0629
    public String findProfilePath(String userId) { //컨트롤러가 건네준 userId를 통해 jpa한테 찾아달라하자!

        User user = userRepository.findById(userId) //아디 줄테니까 유저정보줘
                .orElseThrow();
        //return uploadRootPath + "/" + user.getProfileImg(); //프로필 이미지 경로를 꺼내서 리턴~ -> 리턴된 값이 컨트롤러 filePath로간다.
        return user.getProfileImg(); //0704


    }
}

