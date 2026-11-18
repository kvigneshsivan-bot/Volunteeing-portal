package com.maatram.repository;

import com.maatram.entity.User;
import com.maatram.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByVolunteerIdIgnoreCase(String volunteerId);
    List<User> findByRole(Role role);
}
