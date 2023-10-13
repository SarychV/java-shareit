package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer lastUserId = 0;

    @Override
    public User addUser(User user) {
        Integer newId = ++lastUserId;
        user.setId(newId);
        users.put(newId, user);
        return users.get(newId);
    }

    @Override
    public boolean isNotUniqueEmail(String email) {
        for (User user: users.values()) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }

    @Override
    public User updateUser(Integer id, User user) {
        users.put(id, user);
        return user;
    }

    @Override
    public User getUser(Integer id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d отсутствует в базе.", id));
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Integer id) {
        User user = users.remove(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d отсутствует в базе.", id));
        }
    }
}
