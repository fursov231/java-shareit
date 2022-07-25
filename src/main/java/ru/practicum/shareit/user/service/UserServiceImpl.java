package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.util.EmailValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userStorage.findById(id);
    }

    @Override
    public User createUser(UserDto userDto) {
        if (isEmailExist(userDto.getEmail())) {
            throw new ConflictException("Такой email уже существует");
        }
        if (userDto.getEmail() == null) {
            throw new ValidationException("Не указан email");
        }
        if (!EmailValidator.isValid(userDto.getEmail())) {
            throw new ValidationException("Указан неверный email");
        }
        return userStorage.save(userDto);
    }

    @Override
    public User updateUser(long userId, UserDto userDto) {
        if (isEmailExist(userDto.getEmail())) {
            throw new ConflictException("Такой email уже существует");
        }
            return userStorage.update(userId, userDto);
    }

    @Override
    public boolean removeUser(long userId) {
        return userStorage.delete(userId);
    }

    private boolean isEmailExist(String email) {
        List<User> users = userStorage.findAll();
        return users.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(email));
    }
}
