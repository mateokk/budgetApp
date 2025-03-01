package com.example.budget.mappers;

import com.example.budget.dto.UserRegisterDTO;
import com.example.budget.dto.UserResponseDTO;
import com.example.budget.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterDTO userRegisterDTO);
    UserResponseDTO toUserResponseDTO(User user);
}
