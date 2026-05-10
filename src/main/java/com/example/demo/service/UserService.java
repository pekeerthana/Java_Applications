package com.example.demo.service;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.respository.RoleRepository;
import com.example.demo.respository.UserRepository;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;

@Service
public class UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,RoleRepository roleRepository,
                        UserMapper userMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new DuplicateEmailException("Email is already in use");
        }
        User newUser = userMapper.toEntity(user);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository
        .findByName("ROLE_USER")
        .orElseThrow(() -> new RuntimeException("Default role not found"));

        newUser.getRoles().add(userRole);
        
        User savedUser = userRepository.save(newUser);
        UserResponseDTO userRes = userMapper.toDTO(savedUser);
        return userRes;
    }
    
    public UserResponseDTO getById(Long id){

        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + id)
            );
        UserResponseDTO user_details = new UserResponseDTO(user.getId(),user.getName(),user.getEmail(),user.getCreatedAt());

         return user_details;
            
    }

    public UserResponseDTO getUserByEmail(String email){

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: "+ email));

        UserResponseDTO user_details = new UserResponseDTO(user.getId(),user.getName(),user.getEmail(),user.getCreatedAt());

         return user_details;
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO newUser){
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + id)
            );
        
        user.setName(newUser.getName());
        user.setEmail(newUser.getEmail());
        User savedUser = userRepository.save(user);
        UserResponseDTO updatedUser = new UserResponseDTO(savedUser.getId(),savedUser.getName(),savedUser.getEmail(),savedUser.getCreatedAt());
        return updatedUser;
    }
    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + id)
            );
        userRepository.delete(user);
    }

    public Page<UserResponseDTO> getUsersByPage(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        Page<UserResponseDTO> usersList = users.map(user -> new UserResponseDTO(user.getId(),user.getName(),user.getEmail(),user.getCreatedAt()));
        return usersList;
        
    }

    @Transactional
    public String promoteToAdmin(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Role adminRole = roleRepository
                .findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        boolean alreadyAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!alreadyAdmin) {
            user.getRoles().add(adminRole);
            userRepository.save(user);
        } else {
            return "User is already an admin";
        }

        return "User promoted to ADMIN successfully";
    }


    @Transactional 
    public List<UserResponseDTO> getAllUsers(){

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());        
    }
}
