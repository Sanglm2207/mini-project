package com.kaidev99.training.miniproject.service;

import com.kaidev99.training.miniproject.domain.dto.request.LoginRequest;
import com.kaidev99.training.miniproject.domain.dto.request.RefreshTokenRequest;
import com.kaidev99.training.miniproject.domain.dto.request.SignUpRequest;
import com.kaidev99.training.miniproject.domain.dto.response.JwtAuthResponse;
import com.kaidev99.training.miniproject.domain.enums.Role;
import com.kaidev99.training.miniproject.domain.model.User;
import com.kaidev99.training.miniproject.repository.UserRepository;
import com.kaidev99.training.miniproject.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    /**
     * Registers a new user in the system.
     *
     * @param request The SignUpRequest DTO containing new user's credentials.
     * @return The saved User entity.
     */
    public User signup(SignUpRequest request) {
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) // Always encode the password
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    /**
     * Authenticates a user and provides JWT tokens upon successful login.
     *
     * @param request The LoginRequest DTO containing user's credentials.
     * @return A JwtAuthResponse containing the access and refresh tokens.
     */
    public JwtAuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new JwtAuthResponse(accessToken, refreshToken);
    }

    /**
     * Refreshes an access token using a valid refresh token.
     *
     * @param request The RefreshTokenRequest DTO containing the refresh token.
     * @return A JwtAuthResponse with a new access token and the original refresh token.
     */
    public JwtAuthResponse refreshToken(RefreshTokenRequest request) {
        final String refreshToken = request.token();
        final String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found from refresh token"));

        if (jwtService.isTokenValid(refreshToken, user)) {
            var newAccessToken = jwtService.generateToken(user);
            return new JwtAuthResponse(newAccessToken, refreshToken);
        }

        throw new IllegalArgumentException("Invalid or expired refresh token");
    }
}
