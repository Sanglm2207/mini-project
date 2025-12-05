package com.kaidev99.training.miniproject.domain.dto.response;

import com.kaidev99.training.miniproject.domain.enums.Role;
import com.kaidev99.training.miniproject.domain.model.User;

public record UserResponse(String id, String username, Role role) {

    // Factory method để chuyển đổi từ entity User sang DTO
    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}