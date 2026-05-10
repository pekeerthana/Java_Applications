package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    public LoginRequestDTO(@NotBlank @Email String email, @NotBlank String password) {
        this.email = email;
        this.password = password;
    }
    @NotBlank(message = "email must not be blank")
    @Email
    private String email;
    @NotBlank(message = "password must not be blank")
    private String password;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
