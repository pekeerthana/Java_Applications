package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.Provider.Service;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.respository.UserRepository;
import com.example.demo.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock 
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    
    
    @InjectMocks
    private LoginService loginService;


    @Test
    public void login_Success(){

        LoginRequestDTO login = new LoginRequestDTO("name@test.com", "name@123");

        User user = new User();
        user.setEmail("name@test.com");
        user.setPassword("password");

        when(userRepository.findByEmail(login.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(login.getPassword(),user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(login.getEmail())).thenReturn("token");

        String token = loginService.login(login);

        assertEquals("token", token);
    }

    @Test
    public void login_InvalidPassword() {

        LoginRequestDTO request = new LoginRequestDTO("name@test.com", "wrong");

        User user = new User();
        user.setEmail("name@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            loginService.login(request);
        });
    }


    

    
}
