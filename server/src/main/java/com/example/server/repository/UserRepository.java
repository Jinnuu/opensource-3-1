package com.example.server.repository;

import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//사용자 관련 데이터베이스 조작을 담당하는 리포지토리

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);
    User findByName(String name);
} 