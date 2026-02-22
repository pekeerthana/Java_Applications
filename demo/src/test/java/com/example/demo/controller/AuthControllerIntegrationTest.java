package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.demo.TestDataInitializer;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.respository.RoleRepository;
import com.example.demo.respository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Set;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestDataInitializer.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private final UserRepository _userRepository;
    @Autowired
    private final ObjectMapper _objectMapper;
    @Autowired
    private final MockMvc _mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public AuthControllerIntegrationTest(UserRepository userRepository, ObjectMapper objectMapper, MockMvc mockMvc){

        _userRepository = userRepository;
        _objectMapper = objectMapper;
        _mockMvc = mockMvc;

    }




    @BeforeEach
    void cleanDb() {
        _userRepository.deleteAll();
    }

    @Test
    void userPostRequestSuccess() throws Exception{

        String email = "controller_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");        
        String userString = _objectMapper.writeValueAsString(user);
        _mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userString))
        .andExpect(status().isCreated());

        assertEquals(1, _userRepository.count());        
    }

    @Test
    void duplicateEmailCheck() throws Exception{
        String email = "controller_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");        
        String userString = _objectMapper.writeValueAsString(user);

        _mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isCreated());

        _mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isBadRequest());

        assertEquals(1, _userRepository.count());

    }

    @Test 
    void invalidEmailCheck() throws Exception{
        String email = "controller_" + System.currentTimeMillis() + "@";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");        
        String userString = _objectMapper.writeValueAsString(user);

        _mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userString))
        .andExpect(status().isBadRequest());
        assertEquals(0, _userRepository.count());

    }

    @Test
    void invalidPasswordCheck() throws Exception{
        String email = "controller_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "k@1234");        
        String userString = _objectMapper.writeValueAsString(user);

        _mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userString))
        .andExpect(status().isBadRequest());
        assertEquals(0, _userRepository.count());
    }

    @Test
    void missingFieldCheck() throws Exception{

        UserRequestDTO user_1 = new UserRequestDTO("keero", "","keeee@1234");        
        String userString_1 = _objectMapper.writeValueAsString(user_1);

        _mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userString_1))
        .andExpect(status().isBadRequest());
        assertEquals(0, _userRepository.count());

        UserRequestDTO user_2 = new UserRequestDTO("keero", "keee@test.com");        
        String userString_2 = _objectMapper.writeValueAsString(user_2);

        _mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(userString_2))
        .andExpect(status().isBadRequest());
        assertEquals(0, _userRepository.count());


    }

    @Test
    void successfullLoginTest() throws Exception{

       String email = "controller_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");        
        String userString = _objectMapper.writeValueAsString(user);

        _mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isCreated());
        assertEquals(1, _userRepository.count());

        LoginRequestDTO loginRequest = new LoginRequestDTO(user.getEmail(),user.getPassword());
        String loginString = _objectMapper.writeValueAsString(loginRequest);
        
        MvcResult response = _mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginString))
                .andExpect(status().isOk())
                .andReturn();

        String token = response.getResponse().getContentAsString();
        assertTrue(token != null && !token.isBlank());

    }

    @Test
    void incorrectPasswordCheck() throws Exception{

        String email = "controller_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");        
        String userString = _objectMapper.writeValueAsString(user);

        _mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isCreated());
        assertEquals(1, _userRepository.count());

        LoginRequestDTO loginRequest = new LoginRequestDTO(user.getEmail(),"keer@7646");
        String loginString = _objectMapper.writeValueAsString(loginRequest);
        
         _mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginString))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void nonExistingUserCheck() throws Exception{

        LoginRequestDTO loginRequest = new LoginRequestDTO("sheero@test.com","sheero@7646");
        String loginString = _objectMapper.writeValueAsString(loginRequest);
        
         _mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginString))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void noTokenAuthorizationCheck() throws Exception{

        _mockMvc.perform(get("/auth")
                             .contentType(MediaType.APPLICATION_JSON))
                             .andExpect(status().isForbidden());

    }

    @Test
    void userRoleShouldReturnForbiddenForAdminEndpoint() throws Exception{

        String email = "controller_" + System.currentTimeMillis() + "@test.com";
        UserRequestDTO user = new UserRequestDTO("keero", email, "keero@1234");        
        String userString = _objectMapper.writeValueAsString(user);

        _mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString))
                .andExpect(status().isCreated());
        assertEquals(1, _userRepository.count());

        LoginRequestDTO loginRequest = new LoginRequestDTO(user.getEmail(),user.getPassword());
        String loginString = _objectMapper.writeValueAsString(loginRequest);

        MvcResult response = _mockMvc.perform(post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(loginString))
                                    .andExpect(status().isOk())
                                    .andReturn();

        String token = response.getResponse().getContentAsString();

                 _mockMvc.perform(get("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andDo(print()) 
                .andExpect(status().isForbidden());


    }

    @Test
    void adminRoleShouldAccessAdminEndpointSuccessfully() throws Exception {

        String email = "admin_" + System.currentTimeMillis() + "@test.com";

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow();

        User admin = new User();
        admin.setName("adminUser");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode("admin@1234"));
        admin.setRoles(Set.of(adminRole));

        _userRepository.save(admin);

        // Login as admin
        LoginRequestDTO loginRequest =
                new LoginRequestDTO(email, "admin@1234");

        String loginString =
                _objectMapper.writeValueAsString(loginRequest);

        MvcResult response = _mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginString))
                .andExpect(status().isOk())
                .andReturn();

        String token = response.getResponse().getContentAsString();

        // Access admin endpoint
        _mockMvc.perform(get("/auth")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    
}
