package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.util.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {
    @Autowired
    private final EntityManager em;

    @Autowired
    private final UserService userService;

    private UserRequestDto userRequestDto;

    @BeforeEach
    public void setUp() {
        userRequestDto = UserRequestDto.builder().name("Boris").email("Boris@mail.com").build();
    }


    @Test
    void shouldBeReturnedAllUsers() {
        userService.createUser(userRequestDto);
        userService.createUser(UserRequestDto.builder().name("Ivan").email("Ivan@mail.com").build());

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> result = query.getResultList();
        List<UserResponseDto> foundedUsers = userService.getAllUsers();

        assertThat(result.size(), equalTo(foundedUsers.size()));
        assertThat(result.get(0), equalTo(foundedUsers.get(0)));
        assertThat(result.get(1), equalTo(foundedUsers.get(1)));
    }

    @Test
    void shouldBeReturnedUserById() {
        UserService userServiceMock = Mockito.mock(UserService.class);
        User user = Mockito.mock(User.class);
        when(userServiceMock.getUserById(anyLong())).thenReturn((UserMapper.toUserResponseDto(user)));

        UserResponseDto result = userServiceMock.getUserById(1);

        assertThat(result, equalTo(user));
    }

    @Test
    void shouldBeCreated() {
        userService.createUser(userRequestDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name", User.class);
        User user = query.setParameter("name", userRequestDto.getName())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userRequestDto.getName()));
        assertThat(user.getEmail(), equalTo(userRequestDto.getEmail()));
    }

    @Test
    void shouldBeNotCreatedWithNullEmail() {
        userRequestDto.setEmail(null);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(userRequestDto));

        Assertions.assertEquals(exception.getMessage(), "Не указан email");
    }

    @Test
    void shouldBeNotCreatedWithInvalidEmail() {
        userRequestDto.setEmail("boris.com");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(userRequestDto));

        Assertions.assertEquals(exception.getMessage(), "Указан неверный email");
    }

    @Test
    void shouldBeUpdated() {
        UserResponseDto user = userService.createUser(userRequestDto);
        UserRequestDto updatedDto = UserRequestDto.builder().name("Leo").email("leo@mail.com").build();
        UserResponseDto updatedUser = userService.updateUser(user.getId(), updatedDto);

        assertThat(updatedUser.getName(), equalTo(updatedDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updatedDto.getEmail()));
    }

    @Test
    void shouldBeRemovedUserById() {
        UserService userServiceMock = Mockito.mock(UserService.class);
        doNothing().when(userServiceMock).removeUser(anyLong());

        userServiceMock.removeUser(1);

        verify(userServiceMock, times(1)).removeUser(anyLong());
    }

    @Test
    void shouldBeRemovedUserByIdFromDB() {
        UserResponseDto user = userService.createUser(userRequestDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name", User.class);
        User createdUser = query.setParameter("name", userRequestDto.getName())
                .getSingleResult();
        assertThat(createdUser.getName(), equalTo(user.getName()));

        userService.removeUser(user.getId());
        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> query.setParameter("name", userRequestDto.getName())
                        .getSingleResult());

        Assertions.assertEquals(exception.getMessage(), "No entity found for query");
    }
}