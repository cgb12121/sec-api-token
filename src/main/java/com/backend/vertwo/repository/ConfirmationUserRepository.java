package com.backend.vertwo.repository;

import com.backend.vertwo.entity.user.ConfirmationUser;
import com.backend.vertwo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationUserRepository extends JpaRepository<ConfirmationUser, Long> {
    Optional<ConfirmationUser> findByKey(String key);
    Optional<ConfirmationUser> findByUser(User user);
}