package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Имя пользователя и адрес его электронной почты должны быть заданы.");
        }
        if (userRepository.isNotUniqueEmail(user.getEmail())) {
            throw new ConflictException("Адрес электронной почты пользователя должен быть уникальным.");
        }
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(Integer id, User user) {
        User modifiedUser = userRepository.getUser(id);

        String name = user.getName();
        if (name != null) {
            modifiedUser.setName(name);
        }

        String email = user.getEmail();
        if (userRepository.isNotUniqueEmail(email) && (!modifiedUser.getEmail().equals(email))) {
            throw new ConflictException("Адрес электронной почты пользователя должен быть уникальным.");
        }

        if (email != null) {
            modifiedUser.setEmail(email);
        }
        return userRepository.updateUser(id, modifiedUser);
    }

    @Override
    public User getUser(Integer userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }
}
