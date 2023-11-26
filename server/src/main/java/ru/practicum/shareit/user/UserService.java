package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);

    UserDto updateUser(UserDto userDto);

    UserDto getUser(Integer userId);

    List getAllUsers();

    void deleteUser(Integer userId);
}
