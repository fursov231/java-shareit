package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(long id);

    User createUser(UserDto userDto);

    User updateUser(long userId, UserDto userDto);

    boolean removeUser(long userId);
}
