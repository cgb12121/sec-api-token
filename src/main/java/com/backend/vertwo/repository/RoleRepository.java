package com.backend.vertwo.repository;

import com.backend.vertwo.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByRoleIgnoreCase(String role);
}