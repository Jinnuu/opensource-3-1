package com.example.server.controller;

import com.example.server.entity.ExerciseRoutine;
import com.example.server.entity.User;
import com.example.server.repository.ExerciseRoutineRepository;
import com.example.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//운동 루틴 관련 요청을 처리하는 컨트롤러

@RestController
@RequestMapping("/api/routines")
public class ExerciseRoutineController {

    @Autowired
    private ExerciseRoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createRoutine(@RequestParam String userName, @RequestBody ExerciseRoutine routine) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        routine.setUser(user);
        routineRepository.save(routine);
        return ResponseEntity.ok("운동 루틴이 저장되었습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getRoutines(@RequestParam String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        List<ExerciseRoutine> routines = routineRepository.findByUser(user);
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoutine(@PathVariable Long id, @RequestParam String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        ExerciseRoutine routine = routineRepository.findByIdAndUser(id, user);
        if (routine == null) {
            return ResponseEntity.badRequest().body("운동 루틴을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(routine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoutine(@PathVariable Long id, @RequestParam String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        ExerciseRoutine routine = routineRepository.findByIdAndUser(id, user);
        if (routine == null) {
            return ResponseEntity.badRequest().body("운동 루틴을 찾을 수 없습니다.");
        }

        routineRepository.delete(routine);
        return ResponseEntity.ok("운동 루틴이 삭제되었습니다.");
    }
} 