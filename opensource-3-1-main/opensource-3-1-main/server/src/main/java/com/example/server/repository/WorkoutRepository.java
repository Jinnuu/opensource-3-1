package com.example.server.repository;

import com.example.server.entity.Workout;
import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    @Query("SELECT w FROM Workout w WHERE w.user = ?1 AND w.date BETWEEN ?2 AND ?3 ORDER BY w.date")
    List<Workout> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
} 