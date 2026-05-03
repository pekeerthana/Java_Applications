package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@Validated
@RequestMapping("/auth/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<String> userLogin(@Valid @RequestBody LoginRequestDTO loginRequest) {

        String loginDetails = loginService.login(loginRequest);
        return ResponseEntity.ok(loginDetails);

    }
    

    
}
