package com.backend.vertwo.repository;

import com.backend.vertwo.entity.user.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    @NonNull
    Optional<User> findById(@NonNull Long id);
}