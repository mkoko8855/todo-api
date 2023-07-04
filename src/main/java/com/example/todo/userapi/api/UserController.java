package com.example.todo.userapi.api;

import antlr.Token;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import java.io.File;
import java.io.IOException;

//0622 0623
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;


    //이메일 중복 확인 요청 처리
    //GET: /api/auth/check?email=zzzz@xxx.com
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
        if (email.trim().equals("")) { //매개값으로 넘어온 저 email에 공백제거다해. 그것이 같애. 빈거랑.
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다.");
        }
        boolean resultFlag = userService.isDuplicate(email); //중복이 됐냐 안됐냐?
        log.info("{} 중복?? - {}", email, resultFlag);

        return ResponseEntity.ok().body(resultFlag); //클라이언트로 넘기기.

    }


    //회원 가입 요청 처리
    //POST: /api/auth
    @PostMapping
    public ResponseEntity<?> signup(@Validated @RequestPart("user") UserRequestSignUpDTO dto, @RequestPart(value = "profileImage", required = false) MultipartFile profileImg, BindingResult result) { //signup은 회원가입. in은 로그인 -> 0628수정

        log.info("/api/auth POST - {}", dto);


        if (result.hasErrors()) {
            log.warn(result.toString()); //그냥 결과한번 찍어보고~
            return ResponseEntity.badRequest()
                    .body(result.getFieldError()); //방금 발생한 에러를 꺼내서 리턴해주자.
        }


        try {
            String uploadedFilePath = null; //일단 변수 선언만 -> 프로파일이미지가 null이 아니라면 아래로~
            if (profileImg != null) { //유효성 검사 -> 사용자가 이미지 첨부했다면,
                log.info("attached file name: {}", profileImg.getOriginalFilename());
                uploadedFilePath = userService.uploadProfileImage(profileImg); //로컬경로에 사용자가 첨부한 이미지를 저장하겠다.
            }


            UserSignUpResponseDTO responseDTO = userService.create(dto, uploadedFilePath);//서비스의 create를 부르면서 dto전달해주자. 그리고 지역변수로 삽입 -> create는 업로드파일패쓰도 같이 받게끔.
            return ResponseEntity.ok()
                    .body(responseDTO);
        } catch (NoRegisteredArgumentsException e) {
            log.warn("필수 가입 정보를 전달받지 못했습니다.");
            return ResponseEntity.badRequest() //badRequest말고 status도 할 수 있다. 그냥 badRequest쓰자.
                    .body(e.getMessage());
        } catch (DuplicatedEmailException e) {
            log.warn("이메일이 중복되었습니다.");
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e) { //0628
            log.warn("기타 예외가 발생했습니다.");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }

    //로그인 요청 처리
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Validated @RequestBody LoginRequestDTO dto) { //로그인할때 제이슨 형태로 정보가 날아오겠지

        try {
            LoginResponseDTO responseDTO = userService.authenticate(dto);
            return ResponseEntity.ok().body(responseDTO); //클라이언트로 전송.
        } catch (Exception e) {
            //에러날곳은 authenticate이잖아. 한번보자~
            e.printStackTrace();
            return ResponseEntity.badRequest() //배드리퀘스트가 맞지. 이메일이 없다는건 이메일을잘못적은거고 비번도 마찬가지고.
                    .body(e.getMessage());
        }
    }


    //회원가입하면 common등급은 기본으로 주는데 프리미엄으로 승격할수도있지? 0627
    //즉, 일반 회원을 프리미엄 회원으로 승격하는 요청 처리(권한 검사도같이)
    //crud 중, 수정이겠지. UPDATE작성임.

    //수정이니 풋or패치 맵핑
    @PutMapping("/promote") //로그인을 해야만함
    @PreAuthorize("hasRole('ROLE_COMMON')")
    //얘는, 권한 검사. 즉, 해당 권한이 아니라면 인가처리 거부하고 403 코드를 리턴한다. 0627 -> ROLE_COMMON이 아닌 애들은 다 내친다. 굳이 서비스에서 COMMON이 맞냐? 라고 확인안해도됨. ROLE_ 에 대해선 웹시큐리티클래스에 써놨음.
    public ResponseEntity<?> promote(@AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("/api/auth/promote - PUT!");

        try {
            LoginResponseDTO responseDTO = userService.promoteToPremium(userInfo);
            return ResponseEntity.ok()
                    .body(responseDTO);
        } catch (IllegalStateException | NoRegisteredArgumentsException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //0629
    //프로필 사진 이미지 데이터를 클라이언트에게 응답하는 처리를 해보자 -> 클라이언트가 이미지를 보내달라고 요청하는 메서드
    @GetMapping("/load-profile")
    public ResponseEntity<?> loadfile(@AuthenticationPrincipal TokenUserInfo userInfo) {

        //로그인 성공한 애부터 누군지 알아야하니 토큰(Jwt)을 받아야지.
        //Jwt받으려면 Jwtauthfilter가동작해야지
        //웹시큐리티클래스로 고고 -> 와서,

        log.info("/api/auth/load-profile - GET!, user: {}", userInfo.getEmail());

        try {
            //클라이언트가 요청한 프로필 사진을 응답해야함.
            //누군지알았으니, 프로필 사진을 가져와야지
            //즉, 1. 프로필 사진의 경로를 얻자
            //db에서 조회부터하자 -> 조회하고, 이 유저가 프로필사진을 가지고있는지 null인지 알아보는것.
            String filePath = userService.findProfilePath(userInfo.getUserId());

            //2. 얻어낸 파일 경로를 통해서 실제 파일 데이터 로드하기
            File profileFile = new File(filePath); //File객체생성

            //검사한번하자. 혹시 profileFile이 존재하지 않는 경로라면,
            if (!profileFile.exists()) {
                return ResponseEntity.notFound().build(); //404에러를 보내주자.
            }

            //if문을 건너뛰는건 파일이 있는거니, 해당 경로에 저장된 파일을 바이트배열로 직렬화 해서 리턴시키자. 즉, 클라이언트에게 보내주자
            byte[] fileData = FileCopyUtils.copyToByteArray(profileFile);
            //파일을 제대로 찾았으면 리턴해주자

            //3. 응답 헤더에 컨텐츠 타입을 설정하자
            HttpHeaders headers = new HttpHeaders(); //객체생성

            //헤더스에 값을 키와 밸류 형태로 보내자
            //headers.set("Content-type", "image/jpeg"); //이런식으로해도되고
            //headers.setContentType(MediaType.IMAGE_JPEG); //이렇게 줘도된다. 그러나 메서드화로 부르자. 미디어타입이 png..등등 여러갤수있으니
            MediaType contentType = findExtendsionAndGetMediaType(filePath); //findExtendsion~~은 지은거임그냥.
            if (contentType == null) {
                return ResponseEntity.internalServerError()
                        .body("발견된 파일은 이미지 파일이 아닙니다.");
            }
            headers.setContentType(contentType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("파일을 찾을 수 없습니다.");
        }
    }


    //0629
    private MediaType findExtendsionAndGetMediaType(String filePath) {
        //파일 경로에서 확장자 추출하기
        //C:/todo_upload/asldmaldmlwm_abc.jpg 이런식으로 되어있을테니
        //뒤에서부터 .찾고 거기서부터 짤라줘!
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
        //그럼이제 ext는 jpg만뽑힌다.

        //근데 jpg만 있는게 아니니까,
        switch (ext.toUpperCase()) { //확장자를 추출했을때, 대문자로 변경한 기준으로
            case "JPG":
            case "JPEG":
                return MediaType.IMAGE_JPEG;
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "GIF":
                return MediaType.IMAGE_GIF;
            default:
                return null;
        }
    }


    // S3에서 불러온 프로필 사진 처리 0704
    @GetMapping("/load-s3")
    public ResponseEntity<?> loadS3(@AuthenticationPrincipal TokenUserInfo userInfo) { //비회원이면안되니 토큰달라해
        log.info("/api/auth/load-s3 GET - user: {}", userInfo);


        try {
            String profilePath = userService.findProfilePath(userInfo.getUserId());
            return ResponseEntity.ok().body(profilePath);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

