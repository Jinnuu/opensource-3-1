package com.example.server.repository;

import com.example.server.entity.ExerciseRoutine;
import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExerciseRoutineRepository extends JpaRepository<ExerciseRoutine, Long> {
    List<ExerciseRoutine> findByUser(User user);
    ExerciseRoutine findByIdAndUser(Long id, User user);
} 