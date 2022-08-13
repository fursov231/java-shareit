package ru.practicum.shareit.user.storage;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.user.model.User;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Optional;


public class UserRepositoryImpl implements UserRepositoryCustom {
    private final UserRepository userRepository;

    public UserRepositoryImpl(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SneakyThrows
    @Override
    public User updateById(long userId, User user) {
        Optional<User> oldUser = userRepository.findById(userId);
        User patchedUser = User.builder().build();
        if (oldUser.isPresent()) {
            patchedUser = oldUser.get();
            BeanInfo beanInfo = Introspector.getBeanInfo(User.class);
            for (PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                String propertyName = propertyDesc.getName();
                Object value = propertyDesc.getReadMethod().invoke(user);
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
        return userRepository.save(patchedUser);
    }
}


