package com.maatram.service;

import com.maatram.dto.RegisterRequest;
import com.maatram.entity.Role;
import com.maatram.entity.User;
import com.maatram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String volunteerId = request.volunteerId().trim().toUpperCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (users.existsByVolunteerIdIgnoreCase(volunteerId)) {
            throw new IllegalArgumentException("Volunteer ID is already registered");
        }
        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setVolunteerId(volunteerId);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setMobileNumber(request.mobileNumber());
        user.setAcademicYear(request.academicYear());
        user.setRole(Role.VOLUNTEER);
        try {
            return users.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Email or Volunteer ID is already registered", exception);
        }
    }
}
