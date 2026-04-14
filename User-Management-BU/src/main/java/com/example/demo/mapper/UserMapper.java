package com.example.demo.mapper;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface UserMapper {

    User toEntity(UserRequestDTO dto);

    UserResponseDTO toDTO(User user);
     
}
