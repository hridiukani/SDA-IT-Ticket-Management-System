package com.itoffice.ticketsystem.service;

import com.itoffice.ticketsystem.dto.request.LoginRequest;
import com.itoffice.ticketsystem.dto.request.RegisterRequest;
import com.itoffice.ticketsystem.dto.response.AuthResponse;
import com.itoffice.ticketsystem.dto.response.UserResponse;
import com.itoffice.ticketsystem.model.enums.Role;
import com.itoffice.ticketsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        UserResponse userResponse = userService.createUser(
            request, Role.ROLE_USER);

        UserDetails userDetails = userDetailsService
            .loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService
            .loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        UserResponse userResponse = userService
            .getUserByUsername(request.getUsername());

        return new AuthResponse(token, userResponse);
    }
}
