package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name="permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable=false, unique = true)
    private String name;


    public Permission(String name) {
        this.name = name;
    }

    public Permission() {}
    
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
