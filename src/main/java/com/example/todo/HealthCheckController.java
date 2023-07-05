package com.example.todo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//0705
@RestController
@Slf4j
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<?> healthCheck(){
        log.info("server is running...I am healthy!");
        return ResponseEntity.ok()
                .body("It's OK!");
    }



}
