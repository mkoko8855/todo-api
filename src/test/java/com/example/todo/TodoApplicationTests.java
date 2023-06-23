package com.example.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
class TodoApplicationTests {

	@Test
	void contextLoads() {
	}

	
	@Test
	@DisplayName("토큰 서명 해시값 생성하기")
	void makeSecretKey() {

		SecureRandom random = new SecureRandom(); //객체생성

		byte[] key = new byte[64]; // 64바이트 -> 512비트

		random.nextBytes(key); //우리가만든바이트배열을 key에게 전달~ nextBytes는메서드다.

		String encodedKey = Base64.getEncoder().encodeToString(key);

		System.out.println("\n\n\n");
		System.out.println("encodedKey = " + encodedKey);
		System.out.println("\n\n\n");

	}
	
	
	
	
	
	
	
	
}
