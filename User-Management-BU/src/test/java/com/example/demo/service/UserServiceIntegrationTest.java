package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.RoleType;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.respository.UserRepository;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService _userService;
    @Autowired
    private UserRepository _userRepository;
    @Autowired
    private PasswordEncoder _passwordEncoder;

    @Test
    void shouldCreateUserSuccessfully(){

        long count = _userRepository.count();
        String email = "service_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");
        _userService.createUser(user);

        assertEquals(count + 1, _userRepository.count());
        User useByEmail = _userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found"));
        assertTrue(_passwordEncoder.matches(user.getPassword(),useByEmail.getPassword()));
        assertEquals(useByEmail.getRoles().size(), 1);
        boolean hasUserRole = useByEmail.getRoles().stream().anyMatch(role -> role.getName().equals(RoleType.ROLE_USER.name()));
        assertTrue(hasUserRole);

    }

    @Test
    void duplicateEmailCheck(){

        long count = _userRepository.count();
        UserRequestDTO user = new UserRequestDTO("keera","keerth@test.com","keero@1234");
        _userService.createUser(user);
        assertThrows(DuplicateEmailException.class, ()->{ _userService.createUser(user); });
        assertEquals(count+1, _userRepository.count());

    }
    
}
