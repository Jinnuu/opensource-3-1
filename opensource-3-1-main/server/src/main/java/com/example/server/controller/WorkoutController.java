package com.example.server.controller;

import com.example.server.entity.Workout;
import com.example.server.entity.User;
import com.example.server.repository.WorkoutRepository;
import com.example.server.repository.UserRepository;
import com.example.server.entity.WorkoutData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//운동 기록 관련 요청을 처리하는 컨트롤러

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")  // CORS 설정 추가
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getWorkouts(@RequestParam(required = true) String userName) {
        try {
            User user = userRepository.findByName(userName);
            if (user == null) {
                return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
            }

            List<Workout> workouts = workoutRepository.findByUserOrderByDateDesc(user);
            System.out.println("Found " + workouts.size() + " workouts for user: " + userName);
            return ResponseEntity.ok(workouts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("운동 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping(value = "/weekly", produces = "application/json")
    public ResponseEntity<?> getWeeklyWorkouts(@RequestParam(required = true) String userName) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace(); // 로그 추가
            return ResponseEntity.internalServerError().body("주간 운동 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addWorkout(@RequestParam(required = true) String userName, @RequestBody WorkoutData workoutData) {
        try {
            User user = userRepository.findByName(userName);
            if (user == null) {
                return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
            }

            Workout workout = new Workout();
            workout.setUser(user);
            workout.setExerciseType(workoutData.getExerciseType());
            workout.setDuration(workoutData.getDuration());
            workout.setCompleted(workoutData.isCompleted());
            workout.setRoutineName(workoutData.getRoutineName());
            workout.setExerciseNumber(workoutData.getExerciseNumber());
            workout.setTotalExercises(workoutData.getTotalExercises());
            
            // Date를 LocalDateTime으로 변환
            if (workoutData.getWorkoutDate() != null) {
                workout.setDate(workoutData.getWorkoutDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime());
            } else {
                workout.setDate(LocalDateTime.now());
            }
            
            // 필수 필드 검증
            if (workout.getExerciseType() == null || workout.getExerciseType().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("운동 종류는 필수입니다.");
            }

            System.out.println("Saving workout: " + workout);  // 로그 추가
            Workout savedWorkout = workoutRepository.save(workout);
            return ResponseEntity.ok(savedWorkout);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("운동 데이터 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 