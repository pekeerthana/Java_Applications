package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.enums.PermissionType;
import com.example.demo.enums.RoleType;
import com.example.demo.respository.PermissionRepository;
import com.example.demo.respository.RoleRepository;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository _roleRepository;
    private final PermissionRepository _permissionRepository;

    public DataInitializer(RoleRepository roleRepository,PermissionRepository permissionRepository) {

        _roleRepository = roleRepository;
        _permissionRepository = permissionRepository;
    }


    @Override
    public void run(String... args) throws Exception {

        SendPermissions();

        SendRoles();

        AssignAdminPermission();
        
        AssignUserPermission();
        
    }

    private void SendPermissions(){
        for(var value : PermissionType.values()){
            String name  = value.name();
            if( ! _permissionRepository.existsByName(name)){
                Permission permission = new Permission();
                permission.setName(name);
                _permissionRepository.save(permission);
            }
        }
    }

    private void SendRoles(){
        
        for(var value : RoleType.values()){
            String name = value.name();
            if( ! _roleRepository.existsByName(name)){
                Role role = new Role();
                role.setName(name);
                _roleRepository.save(role);
            }
        }
    }
 
    private void AssignAdminPermission() {

        Role role = _roleRepository.findByName(RoleType.ROLE_ADMIN.name()).orElseThrow(() -> new RuntimeException("Role not found"));

        if(role.getPermissions().isEmpty()){
            Permission createPermission = _permissionRepository.findByName(PermissionType.CREATE_USERS.name())
                                            .orElseThrow(()-> new RuntimeException("Persmission not found"));
            Permission deletePermission = _permissionRepository.findByName(PermissionType.DELETE_USERS.name())
                                            .orElseThrow(()-> new RuntimeException("Persmission not found"));
            Permission readPermission = _permissionRepository.findByName(PermissionType.READ_USERS.name())
                                        .orElseThrow(()-> new RuntimeException("Persmission not found"));

            role.getPermissions().add(readPermission);
            role.getPermissions().add(deletePermission);
            role.getPermissions().add(createPermission);
            _roleRepository.save(role);
        }

        
    }

    private void AssignUserPermission(){

        Role user = _roleRepository.findByName(RoleType.ROLE_USER.name()).orElseThrow(() -> new RuntimeException("Role not found"));

        if(user.getPermissions().isEmpty()){
            Permission readPermission = _permissionRepository.findByName(PermissionType.READ_USERS.name())
                                        .orElseThrow(()-> new RuntimeException("Persmission not found"));

            user.getPermissions().add(readPermission);
            _roleRepository.save(user);
        }
    }
}
