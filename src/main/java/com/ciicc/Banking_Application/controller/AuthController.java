package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.BankResponse;
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

@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        exposedHeaders = "*",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<BankResponse> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Use UserService to handle login logic (audits, token, etc.)
            BankResponse response = userService.login(request);

            // If login failed, return with proper status
            if (!"200".equals(response.getResponseCode())) {
                return ResponseEntity.status(401).body(response);
            }

            // Optionally, attach JWT if UserService.login doesn't already include it
            User user = userService.findByIdentifier(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            String token = jwtUtil.generateToken(user.getEmail());

            LoginResponse loginResponse = LoginResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .token(token)
                    .build();

            // Wrap LoginResponse in BankResponse
            BankResponse finalResponse = BankResponse.builder()
                    .responseCode("200")
                    .responseMessage("Login successful!")
                    .data(loginResponse)
                    .build();

            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(BankResponse.unauthorized("Invalid email or password"));
        }
    }
}
