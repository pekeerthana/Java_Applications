package com.example.demo;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.enums.PermissionType;
import com.example.demo.enums.RoleType;
import com.example.demo.respository.PermissionRepository;
import com.example.demo.respository.RoleRepository;

@TestConfiguration
public class TestDataInitializer {

    @Bean
    CommandLineRunner initTestData(RoleRepository roleRepository,
                                   PermissionRepository permissionRepository) {
        return args -> {

            Permission readUsers = permissionRepository
                    .findByName(PermissionType.READ_USERS.name())
                    .orElseGet(() ->
                            permissionRepository.save(
                                    new Permission(PermissionType.READ_USERS.name())
                            )
                    );

            Permission createUsers = permissionRepository
                    .findByName(PermissionType.CREATE_USERS.name())
                    .orElseGet(() ->
                            permissionRepository.save(
                                    new Permission(PermissionType.CREATE_USERS.name())
                            )
                    );

            Permission deleteUsers = permissionRepository
                    .findByName(PermissionType.DELETE_USERS.name())
                    .orElseGet(() ->
                            permissionRepository.save(
                                    new Permission(PermissionType.DELETE_USERS.name())
                            )
                    );

            if (!roleRepository.existsByName(RoleType.ROLE_USER.name())) {
                Role userRole = new Role();
                userRole.setName(RoleType.ROLE_USER.name());
                userRole.setPermissions(Set.of(readUsers));
                roleRepository.save(userRole);
            }

            if (!roleRepository.existsByName(RoleType.ROLE_ADMIN.name())) {
                Role adminRole = new Role();
                adminRole.setName(RoleType.ROLE_ADMIN.name());
                adminRole.setPermissions(Set.of(readUsers, createUsers, deleteUsers));
                roleRepository.save(adminRole);
            }
        };
    }
}