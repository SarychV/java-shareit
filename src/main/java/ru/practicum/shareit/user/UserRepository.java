package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(Integer id, User user);

    User getUser(Integer id);

    List<User> getAllUsers();

    void deleteUser(Integer id);

    boolean isNotUniqueEmail(String email);
}