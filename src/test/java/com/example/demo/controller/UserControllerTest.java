package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    public void postUser_Sucessful() throws Exception{

        UserRequestDTO userRequest = new UserRequestDTO("name","name@test.com","name@1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String request= objectMapper.writeValueAsString(userRequest);

        UserResponseDTO response = new UserResponseDTO(1L,"name","name@test.com",LocalDateTime.now());

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("name@test.com"));
    }


    @Test
    public void postUser_BlankName() throws Exception{

        UserRequestDTO userRequest = new UserRequestDTO("","name@test.com","name@1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String request= objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name should not be blank"))
                .andDo(print());
        
        verify(userService,never()).createUser(any());

    }

    @Test
    public void postUser_BlankEmail() throws Exception{


        UserRequestDTO userRequest = new UserRequestDTO("name","","name@1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String request= objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email should not be blank"));
        
        verify(userService,never()).createUser(any());

    }

    @Test
    public void postUser_InvalidPassword() throws Exception{


        UserRequestDTO userRequest = new UserRequestDTO("name","name@test.com","name");
        ObjectMapper objectMapper = new ObjectMapper();
        String request= objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.message").value("password must be at least 8 characaters"));
        
        verify(userService,never()).createUser(any());

    }

    @Test
    public void postUser_DuplicateEmail() throws Exception{

        UserRequestDTO userRequest = new UserRequestDTO("name","name@test.com","name@1234");
        ObjectMapper objectMapper = new ObjectMapper();
        String request= objectMapper.writeValueAsString(userRequest);

        when(userService.createUser(any())).thenThrow(DuplicateEmailException.class);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isBadRequest());

        verify(userService).createUser(any());

    }

    @Test
    public void getUserById_OkRequest() throws Exception{

        Long id = 1L;
        UserResponseDTO response = new UserResponseDTO(id, "", "", LocalDateTime.now());

        when(userService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/auth/users/{id}",id))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        verify(userService).getById(id);

    }

    @Test
    public void getUserById_BadRequest() throws Exception{

        Long id = 1L;

        when(userService.getById(id)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/auth/users/{id}",id))

                .andExpect(status().isNotFound());

        verify(userService).getById(id);

    }

    @Test
    public void getUserByEmail_OkRequest() throws Exception{

        String email = "name@test.com";
        UserResponseDTO response = new UserResponseDTO(1L, "",email, LocalDateTime.now());

        when(userService.getUserByEmail(email)).thenReturn(response);

        mockMvc.perform(get("/auth/email/{email}",email))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(userService).getUserByEmail(email);

    }


    @Test
    public void updateUser_OkRequest() throws Exception{

        Long id = 1L;
        UserRequestDTO userRequest = new UserRequestDTO("name","name@test.com","name@123");
        ObjectMapper objectMapper = new ObjectMapper();
        String request= objectMapper.writeValueAsString(userRequest);
        UserResponseDTO response = new UserResponseDTO(1L,"name","name@test.com",LocalDateTime.now());

        when(userService.updateUser(eq(id), any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/auth/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("name@test.com"));

        verify(userService).updateUser(eq(id), any(UserRequestDTO.class));

    }

    
    @Test
    public void updateUser_NotFound() throws Exception {

        Long id = 1L;
        UserRequestDTO requestDto = new UserRequestDTO("name", "name@test.com", "pass@0123");
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(requestDto);

        when(userService.updateUser(eq(id), any(UserRequestDTO.class)))
            .thenThrow(UserNotFoundException.class);

        mockMvc.perform(put("/auth/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))

                .andExpect(status().isNotFound())
                .andDo(print());

        verify(userService).updateUser(eq(id), any(UserRequestDTO.class));
    }

    @Test
    public void deleteUser_Success() throws Exception {

        Long id = 1L;

        mockMvc.perform(delete("/auth/{id}", id))
                .andExpect(status().isOk());

        verify(userService).deleteUser(id);
    }

    @Test
    public void deleteUser_NotFound() throws Exception {

        Long id = 1L;

        doThrow(new UserNotFoundException("User not found"))
            .when(userService).deleteUser(id);

        mockMvc.perform(delete("/auth/{id}", id))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(id);
    }


    @Test
    public void getUsersByPage_Success() throws Exception {

        UserResponseDTO user = new UserResponseDTO(1L, "name", "name@test.com", LocalDateTime.now());

        Page<UserResponseDTO> page =
            new PageImpl<>(List.of(user));

        when(userService.getUsersByPage(any())).thenReturn(page);

        mockMvc.perform(get("/auth?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("name@test.com"));

        verify(userService).getUsersByPage(any());
    }

    @Test
    public void promoteUser_Success() throws Exception {

        Long id = 1L;

        when(userService.promoteToAdmin(id))
            .thenReturn("User promoted to ADMIN successfully");

        mockMvc.perform(put("/auth/{id}/promote", id))
                .andExpect(status().isOk())
                .andExpect(content().string("User promoted to ADMIN successfully"));

        verify(userService).promoteToAdmin(id);
    }

    @Test
    public void promoteUser_NotFound() throws Exception {

        Long id = 1L;

        when(userService.promoteToAdmin(id))
            .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/auth/{id}/promote", id))
                .andExpect(status().isNotFound());

        verify(userService).promoteToAdmin(id);
    }

    @Test
    public void getAllUsers_Success() throws Exception {

        UserResponseDTO user =
            new UserResponseDTO(1L, "name", "name@test.com", LocalDateTime.now());

        when(userService.getAllUsers())
            .thenReturn(List.of(user));

        mockMvc.perform(get("/auth/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("name@test.com"));

        verify(userService).getAllUsers();
    }







    
}
