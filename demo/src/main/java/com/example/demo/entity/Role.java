package com.example.demo.entity;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name="role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false,unique = true)
    private String name;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name="role_id"),
        inverseJoinColumns = @JoinColumn(name= "permission_id")

   )

   private Set<Permission> permissions = new HashSet<>();

   public Set<Permission> getPermissions() {
    return permissions;
   }
   public void setPermissions(Set<Permission> permissions) {
    this.permissions = permissions;
   }

    
}
