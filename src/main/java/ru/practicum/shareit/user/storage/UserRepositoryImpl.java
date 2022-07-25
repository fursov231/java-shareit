package ru.practicum.shareit.user.storage;

import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.util.UserMapper;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    long id;
    List<User> userStorage = new ArrayList<>();

    @Override
    public List<User> findAll() {
        return userStorage;
    }

    @Override
    public Optional<User> findById(long id) {
        return userStorage.stream().filter(e -> e.getId() == id).findFirst();
    }

    @Override
    public User save(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        user.setId(++id);
        userStorage.add(user);
        return user;
    }

    @SneakyThrows
    @Override
    public User patch(long userId, UserDto userDto) {
        Optional<User> oldUser = findById(userId);
        User patchedUser = User.builder().build();
        if (oldUser.isPresent()) {
            patchedUser = oldUser.get();
            BeanInfo beanInfo = Introspector.getBeanInfo(UserDto.class);
            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                String propertyName = propertyDesc.getName();
                Object value = propertyDesc.getReadMethod().invoke(userDto);
                if (value != null) {
                    if (propertyName.equals("name")) {
                        patchedUser.setName((String) value);
                    }
                    if (propertyName.equals("email")) {
                        patchedUser.setEmail((String) value);
                    }
                }
            }
        }
        return patchedUser;
    }

    @Override
    public boolean delete(long userId) {
        return userStorage.removeIf(e -> e.getId() == userId);
    }
}

