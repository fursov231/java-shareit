package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.util.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserResponseDto).collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toUserResponseDto(user.get());
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        return UserMapper.toUserResponseDto(userRepository.save(UserMapper.toUser(userRequestDto)));
    }

    @Transactional
    @Override
    public UserResponseDto updateUser(long userId, UserRequestDto userRequestDto) {
        return UserMapper.toUserResponseDto(userRepository.updateById(userId, UserMapper.toUser(userRequestDto)));
    }

    @Transactional
    @Override
    public HttpStatus removeUser(long userId) {
        userRepository.deleteById(userId);
        return HttpStatus.OK;
    }
}
