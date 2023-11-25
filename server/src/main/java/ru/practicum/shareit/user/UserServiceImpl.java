package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new ValidationException("Задайте имя пользователя или адрес электронной почты.");
        }

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        int userId = user.getId();

        User modifiedUser =  userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id=%d отсутствует в базе.", userId)));

        String name = user.getName();
        if (name != null) {
            modifiedUser.setName(name);
        }

        String email = user.getEmail();
        // В хранилище может быть такой же адрес электронной почты только у самого объекта.
        // Проверка сработает если в хранилище есть такой адрес электронной почты и он не у этого объекта.
        if (userRepository.findByEmailContainingIgnoreCase(email).isPresent()
                && (!modifiedUser.getEmail().equals(email))) {
            throw new ConflictException("Email пользователя должен быть уникальным.");
        }
        if (email != null) {
            modifiedUser.setEmail(email);
        }

        return UserMapper.toUserDto(userRepository.save(modifiedUser));
    }

    @Override
    public UserDto getUser(Integer userId) {
        return UserMapper.toUserDto(
                userRepository.findById(userId).orElseThrow(
                        () -> new NotFoundException(
                                String.format("Пользователь с id=%d отсутствует в базе.", userId))));
    }

    @Override
    public List getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}
