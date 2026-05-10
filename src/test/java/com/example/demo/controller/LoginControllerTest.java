package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;



    @Test
    public void userLogin_OkRequest() throws Exception{

        LoginRequestDTO request = new LoginRequestDTO("name@test.com", "name@123");
        ObjectMapper objectMapper = new ObjectMapper();
        String loginRequest = objectMapper.writeValueAsString(request); 

        when(loginService.login(any(LoginRequestDTO.class))).thenReturn("mock-token");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))

                .andExpect(status().isOk())
                .andExpect(content().string("mock-token"));

        verify(loginService).login(any());
                
    }


    @Test
    public void userLogin_NotFound() throws Exception{

        LoginRequestDTO request = new LoginRequestDTO("name@test.com", "name@123");
        ObjectMapper objectMapper = new ObjectMapper();
        String loginRequest = objectMapper.writeValueAsString(request); 


        when(loginService.login(any(LoginRequestDTO.class))).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))

                .andExpect(status().isUnauthorized())
                .andDo(print());
        
        verify(loginService).login(any());
    }

    @Test
    public void userLogin_BadRequest() throws Exception{

        LoginRequestDTO request = new LoginRequestDTO("", "password");
        ObjectMapper objectMapper = new ObjectMapper();
        String loginRequest = objectMapper.writeValueAsString(request); 

         mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email must not be blank"))
                .andDo(print());
    


        verify(loginService,never()).login(any());
    }
   
}
