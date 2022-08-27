package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;

@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:file:./db/shareit-test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryImplTest {
    @Autowired
    private final TestEntityManager em;

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldBeUpdated() {
        User savedUser = userRepository.save(User.builder().id(1L).name("Ivan").email("Ivan@mail.com").build());
        User updatedUser = userRepository.updateById(savedUser.getId(), User.builder().id(1L).name("Boris").email("boris@mail.com").build());

        TypedQuery<User> query = em.getEntityManager()
                .createQuery("select u from User u " +
                        "where u.email = :email", User.class);
        User result = query.setParameter("email", updatedUser.getEmail()).getSingleResult();

        Assertions.assertEquals(result.getId(), savedUser.getId());
        Assertions.assertEquals(result.getEmail(), updatedUser.getEmail());
    }
}