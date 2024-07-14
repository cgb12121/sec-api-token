package com.backend.vertwo.repository;

import com.backend.vertwo.entity.user.CredentialUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialUserRepository extends JpaRepository<CredentialUser, Long> {
  Optional<CredentialUser> findCredentialUserById(Long userId);
}