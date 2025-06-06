package com.example.server.repository;

import com.example.server.entity.Workout;
import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

//운동 기록 관련 데이터베이스 조작을 담당하는 리포지토리

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserOrderByDateDesc(User user);
    
    @Query("SELECT w FROM Workout w WHERE w.user = ?1 AND w.date BETWEEN ?2 AND ?3 ORDER BY w.date DESC")
    List<Workout> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
} 