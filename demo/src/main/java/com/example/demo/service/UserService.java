package com.example.demo.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.respository.UserRepository;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.UserNotFoundException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new DuplicateEmailException("Email is already in use");
        }
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(newUser);
        UserResponseDTO userRes = new UserResponseDTO(savedUser.getId(),savedUser.getName(),savedUser.getEmail(),savedUser.getCreatedAt());
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
}
