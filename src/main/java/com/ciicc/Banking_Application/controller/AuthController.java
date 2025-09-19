package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.LoginResponse;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.security.JwtUtil;
import com.ciicc.Banking_Application.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // Authenticate the user (Spring Security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByIdentifier(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Build response DTO
        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .accountNumber(user.getAccountNumber())
                .role(user.getRole()) // assuming role is enum
                .status(user.getStatus())
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }
}
