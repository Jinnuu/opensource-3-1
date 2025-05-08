package com.example.server.controller;

import com.example.server.entity.Workout;
import com.example.server.entity.User;
import com.example.server.repository.WorkoutRepository;
import com.example.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyWorkouts(@RequestParam String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(6, ChronoUnit.DAYS).withHour(0).withMinute(0).withSecond(0);
        
        List<Workout> workouts = workoutRepository.findByUserAndDateBetween(user, startDate, endDate);
        List<Map<String, Object>> weeklyData = new ArrayList<>();

        // 7일치 데이터 초기화
        for (int i = 0; i < 7; i++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startDate.plusDays(i).toLocalDate().toString());
            dayData.put("duration", 0.0);
            weeklyData.add(dayData);
        }

        // 운동 기록 집계
        for (Workout workout : workouts) {
            int dayIndex = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), workout.getDate().toLocalDate());
            if (dayIndex >= 0 && dayIndex < 7) {
                Map<String, Object> dayData = weeklyData.get(dayIndex);
                double currentDuration = (double) dayData.get("duration");
                dayData.put("duration", currentDuration + workout.getDuration());
            }
        }

        return ResponseEntity.ok(weeklyData);
    }

    @PostMapping
    public ResponseEntity<?> addWorkout(@RequestParam String userName, @RequestBody Workout workout) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        workout.setUser(user);
        workout.setDate(LocalDateTime.now());
        workoutRepository.save(workout);

        return ResponseEntity.ok("운동 기록이 저장되었습니다.");
    }
} 