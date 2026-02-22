package com.example.demo.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "user_id")
    private Long id;
    @Column(nullable = false)
    @NotBlank(message = "name should not be blank")
    private String name;
    @Column(nullable=false, unique = true)
    @Email
    @NotBlank(message = "email should not be blank")
    private String email;
    @Column(nullable=false)
    @NotBlank(message = "password should not be blank")
    @Size(min = 8, message="password must be at least 8 characaters")
    private String password;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long userId) {
        this.id = userId;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns =  @JoinColumn(name="user_id"),
        inverseJoinColumns =  @JoinColumn(name="role_id")
    )

    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return roles;
    }


    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
}
