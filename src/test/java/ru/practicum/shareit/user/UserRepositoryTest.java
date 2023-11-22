package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    int user1Id;
    int user2Id;

    User user1 = new User();
    User user2 = new User();

    @BeforeEach
    void init() {
        user1.setName("user1");
        user1.setEmail("USER1@EMAIL.CO");
        user2.setName("user2");
        user2.setEmail("User2@Email.Co");
    }

    @Test
    void addUser() {

        assertNull(user1.getId());
        assertNull(user2.getId());

        User savedUser = userRepository.save(user1);
        assertNotNull(savedUser.getId());
        user1Id = savedUser.getId();

        savedUser = userRepository.save(user2);
        assertNotNull(savedUser.getId());
        user2Id = savedUser.getId();

        User user1Saved = null;
        Optional<User> user1FromBase = userRepository.findById(user1Id);
        if (user1FromBase.isPresent()) {
            user1Saved = user1FromBase.get();
        }
        assertEquals(user1, user1Saved);

        User user2Saved = null;
        Optional<User> user2FromBase = userRepository.findById(user2Id);
        if (user2FromBase.isPresent()) {
            user2Saved = user2FromBase.get();
        }
        assertEquals(user2, user2Saved);
    }

    @Test
    void findUserByEmailContainingIgnoreCase() {
        User userFind = null;
        Optional<User> user = userRepository.findByEmailContainingIgnoreCase("user1");
        if (user.isPresent()) {
            userFind = user.get();
        }
        assertEquals(user1.getEmail(), userFind.getEmail());
    }
}