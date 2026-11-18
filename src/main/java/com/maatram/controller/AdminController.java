package com.maatram.controller;

import com.maatram.dto.AdminRoleRequest;
import com.maatram.dto.UserResponse;
import com.maatram.entity.Role;
import com.maatram.entity.User;
import com.maatram.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository users;

    @GetMapping("/users")
    public List<UserResponse> users() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @PatchMapping("/users/{email}/role")
    public UserResponse updateRole(@PathVariable String email,
                                   @Valid @RequestBody AdminRoleRequest request) {
        User user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User was not found"));
        if (request.role() == Role.SUPER_ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            user.setRole(Role.SUPER_ADMIN);
        } else if (request.role() != Role.SUPER_ADMIN) {
            user.setRole(request.role());
        }
        return UserResponse.from(users.save(user));
    }
}
