package com.example.demo.respository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Permission;


public interface PermissionRepository extends JpaRepository<Permission,Long>{

    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    
} 