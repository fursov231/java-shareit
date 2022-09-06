package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(long id);

    UserResponseDto createUser(UserRequestDto user);

    UserResponseDto updateUser(long userId, UserRequestDto user);

    void removeUser(long userId);
}
