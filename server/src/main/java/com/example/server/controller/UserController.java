package com.example.server.controller;

import com.example.server.entity.User;
import com.example.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByName(user.getName())) {
            return ResponseEntity.badRequest().body("이미 존재하는 이름입니다.");
        }
        
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User foundUser = userRepository.findByName(user.getName());
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("존재하지 않는 사용자입니다.");
        }
        
        if (!foundUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
        
        return ResponseEntity.ok("로그인 성공");
    }

    @GetMapping("/registration-date")
    public ResponseEntity<?> getRegistrationDate(@RequestParam String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("registrationDate", user.getRegistrationDate().toLocalDate().toString());
        return ResponseEntity.ok(response);
    }
} 