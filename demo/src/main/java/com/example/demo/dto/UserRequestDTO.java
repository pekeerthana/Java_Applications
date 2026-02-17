package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {

    @NotBlank(message = "name should not be blank")
    private String name;
    @Email
    @NotBlank(message = "email should not be blank")
    private String email;
    @NotBlank(message = "password should not be blank")
    @Size(min = 8, message="password must be at least 8 characaters")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public UserRequestDTO(){}
    public UserRequestDTO(String name,String email,String password){

        this.name = name;
        this.email = email;
        this.password = password;

    }

    public UserRequestDTO(String name, String email){
        this(name, email, null);
    }
    
}

