package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(Integer id, User user);

    User getUser(Integer userId);

    List<User> getAllUsers();

    void deleteUser(Integer userId);
}
