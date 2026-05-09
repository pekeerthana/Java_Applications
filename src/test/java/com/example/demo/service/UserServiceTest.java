package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.respository.RoleRepository;
import com.example.demo.respository.UserRepository;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserService userService;


    @Test
    public void createUser_Success(){

        UserRequestDTO newUser = new UserRequestDTO("name","name@test.com","name@123");
        User user =userMapper.toEntity(newUser);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Role role = new Role();
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER"))
                    .thenReturn(Optional.of(role));
        
        UserResponseDTO userRes = userService.createUser(newUser);

        assertEquals(newUser.getEmail(), userRes.getEmail());


    }

    @Test
    public void createUser_Failure(){

        UserRequestDTO newUser = new UserRequestDTO("name","name@test.com","name@123");

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(true);
        
        assertThrows(DuplicateEmailException.class,()->{
                        userService.createUser(newUser);
                });
    }

    @Test
    public void createUserRole_Failure(){

        UserRequestDTO newUser = new UserRequestDTO("name","name@test.com","name@123");

        when(roleRepository.findByName("ROLE_USER"))
                            .thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class,()->{
                        userService.createUser(newUser);
             });

    }

    @Test
    public void getById_Success(){

        Long id = 1L;
        Set<Role> roles = new HashSet<>();
        User user = new User("name","name@test.com","password",roles);
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponseDTO user_details = userService.getById(id);
                        
        assertEquals(id, user_details.getId());
        assertEquals(user.getEmail(), user_details.getEmail());

    }

    @Test
    public void getById_Failure(){

        Long id =1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()->{
                    userService.getById(id);
        });

        assertEquals("User not found with id: "+id,ex.getMessage() );

    }

    @Test
    public void getUserByEmail_Success(){

        String email = "name@test.com";

        Set<Role> roles = new HashSet<>();
        User user = new User("name",email,"password",roles);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserResponseDTO user_details = userService.getUserByEmail(email);

        assertEquals(email, user_details.getEmail());


    }

    @Test
    public void getUserByEmail_Failure(){

        String email = "name@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,()->{
                userService.getUserByEmail(email);
        });

        assertEquals("User not found with email: "+email,ex.getMessage() );
    }

    @Test
    public void updatedUser_Success(){

        Long id = 1L;
        UserRequestDTO user = new UserRequestDTO("name","name@tes.com","name@123");
        User updatedUser = userMapper.toEntity(user);
        updatedUser.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(updatedUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);


        UserResponseDTO savedUser = userService.updateUser(id, user);
        
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getName(), savedUser.getName());


    }

    @Test
    public void updatedUser_Failure(){

        Long id = 1L;
        UserRequestDTO user = new UserRequestDTO("name","name@tes.com","name@123");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException ex =   assertThrows(UserNotFoundException.class,()-> {
            userService.updateUser(id, user);
        });

        assertEquals("User not found with id: "+id,ex.getMessage() );

    }

    @Test
    public void promoteToAdmin_Success(){

        Long id = 1L;
        User user = new User();
        user.setId(id);
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String message = userService.promoteToAdmin(id);

        assertEquals("User promoted to ADMIN successfully",message);
        assertTrue(user.getRoles().contains(role));
        
    }

    @Test
    public void promoteToAdmin_UserNotFound() {

        Long id = 1L;

        when(userRepository.findById(id))
            .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.promoteToAdmin(id);
        });
    }

    @Test
    public void promoteToAdmin_RoleNotFound() {

        Long id = 1L;

        User user = new User();
        user.setId(id);

        when(userRepository.findById(id))
            .thenReturn(Optional.of(user));

        when(roleRepository.findByName("ROLE_ADMIN"))
            .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.promoteToAdmin(id);
        });
    }

    @Test
    public void deleteUser_Success() {

        Long id = 1L;

        User user = new User();
        user.setId(id);

        when(userRepository.findById(id))
            .thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).delete(user);
    }

    @Test
    public void deleteUser_Failure() {

        Long id = 1L;

        when(userRepository.findById(id))
            .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(id);
        });
    }

    @Test
    public void getUsersByPage_Success() {

        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@test.com");

        List<User> userList = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(userList);

        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findAll(pageable))
            .thenReturn(userPage);

        Page<UserResponseDTO> result = userService.getUsersByPage(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("user1@test.com", result.getContent().get(0).getEmail());
        assertEquals("user2@test.com", result.getContent().get(1).getEmail());
    }


    @Test
    public void getAllUsers_Success() {

        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@test.com");

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1@test.com", result.get(0).getEmail());
        assertEquals("user2@test.com", result.get(1).getEmail());
    }

    @Test
    public void getAllUsers_EmptyList() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }








    
}


