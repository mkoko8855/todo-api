package com.example.todo.aws;
//0704

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.PostConstruct;

@Service //마땅히없으면 컴포넌트써도됨
@Slf4j
public class S3Service {


    //s3 버킷을 제어하는 객체이다.
    private S3Client s3;

    @Value("${aws.credentials.accessKey}") //value아노테이션을 통해 프로퍼티 방식으로 끌고온다!
    private String accessKey; //야믈에서끌고올거임

    @Value("${aws.credentials.secretKey}")
    private String secretKey; //야믈에서끌고올거임

    @Value("${aws.region}")
    private String region; //야믈에서끌고올거임
    
    @Value("${aws.bucketName}")
    private String bucketName; //야믈에서끌고올거임
    

    @PostConstruct //서비스 객체가 생성된 후 자동으로 호출되게하는 아노테이션
    //변수를 다 선언했으니 이제 S3에 연결(접속) 해서 인증을 처리하는 로직을 만들자
    private void initializeAmazon(){
        //위에서 준비한 액세스 키와 시크릿 키를 이용하여 계정 인증 받기
        //라이브러리(AwsBasicCredentials)를 준비 했으니 요걸로처리하면된다.
        AwsBasicCredentials credentials
                = AwsBasicCredentials.create(accessKey, secretKey); //인증실패하면 예외발생되긴함..

        //인증 정보를 받았으니 맨 위에서 선언한 s3 변수(객체)를 이용하여 초기화하자.
        this.s3 = S3Client.builder()
                .region(Region.of(region)) //뭘주느냐? 리전은 타입이 리전이라는 이넘타입이다.
                .credentialsProvider(StaticCredentialsProvider.create(credentials)) //위에서 만든 인증 정보인 credentials를 주자.
                .build();
    }

    //이제  로그인 한 거니, 이미지 추가한 다음 url을 받아주는 메서드도 선언해주자.
    /**
     * 버킷에 파일을 업로드하고, 업로드한 버킷의 url 정보를 리턴.
     * @param uploadFile -> 업로드 할 파일의 실제 raw 데이터
     * @param fileName -> 업로드 할 파일 명을 의미한다.
     * @return -> 버킷에 저장된 후, 버킷의 업로드 된 url을 받을 것이다.
     */
    public String uploadToS3Bucket(byte[] uploadFile, String fileName){ //매개변수로는 이미지의 손상이없도록 바이트배열로받자

        //바이트배열로 업로드파일을 받았으니, S3 객체로 생성하자
        PutObjectRequest request
                = PutObjectRequest.builder()
                .bucket(bucketName) //버켓이름전달.
                .key(fileName) //파일 명 전달. 이 Key는 액세스키가 아니다.
                .build();

        //그럼 이제 오브젝트를 버킷에 업로드해야지. s3client를 이용해서.
        s3.putObject(request, RequestBody.fromBytes(uploadFile));


        //업로드 된 파일을 url을 반환하자


        return s3.utilities()
                .getUrl(b -> b.bucket(bucketName).key(fileName))
                .toString()
                ;
    }









}
