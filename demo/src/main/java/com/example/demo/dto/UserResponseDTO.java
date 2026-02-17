package com.example.demo.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {

    private String name;
    private String email;
    private Long id;
    private LocalDateTime createdAt;


    public UserResponseDTO(){}

    public UserResponseDTO(Long id, String name,String email, LocalDateTime createdAt){

        this.name = name;
        this.email = email;
        this.id = id;
        this.createdAt = createdAt;
    }


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

    public Long getId(){
        return id;
    }

    public LocalDateTime getCreatedTime(){
        return createdAt;
    }


    

     
    
}
