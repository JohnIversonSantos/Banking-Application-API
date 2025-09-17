package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class UserControllerTest {

//    private MockMvc mockMvc;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void testRegisterUser() throws Exception {
//        UserRequest request = new UserRequest();
//        request.setFirstName("John");
//        request.setLastName("Doe");
//        request.setEmail("john@example.com");
//        request.setPhoneNumber("09171234567");
//        request.setPassword("Password123");
//
//        BankResponse response = BankResponse.created("Account created successfully!", null);
//        when(userService.createAccount(any(UserRequest.class))).thenReturn(response);
//
//        mockMvc.perform(post("/api/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("Account created successfully!"));
//
//        verify(userService, times(1)).createAccount(any(UserRequest.class));
//    }
//
//    @Test
//    void testLoginUser() throws Exception {
//        LoginRequest request = new LoginRequest();
//        request.setIdentifier("john@example.com");
//        request.setPassword("Password123");
//
//        BankResponse response = BankResponse.success("Login successful!", null);
//        when(userService.login(any(LoginRequest.class))).thenReturn(response);
//
//        mockMvc.perform(post("/api/users/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Login successful!"));
//
//        verify(userService, times(1)).login(any(LoginRequest.class));
//    }
}
