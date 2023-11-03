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
    private final UserRepository userJpaRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getEmail() == null) {
            throw new ValidationException("Имя пользователя и адрес его электронной почты должны быть заданы.");
        }

        User user = UserMapper.toUser(userDto);
        user.hasValidEmailOrThrow();
        return UserMapper.toUserDto(userJpaRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        int userId = user.getId();
        user.hasValidEmailOrThrow();

        User modifiedUser =  userJpaRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь с id=%d отсутствует в базе.", userId)));

        String name = user.getName();
        if (name != null) {
            modifiedUser.setName(name);
        }

        String email = user.getEmail();
        // В хранилище может быть такой же адрес электронной почты только у самого объекта.
        // Проверка сработает если в хранилище есть такой адрес электронной почты и он не у этого объекта.
        if (userJpaRepository.findByEmailContainingIgnoreCase(email).isPresent()
                && (!modifiedUser.getEmail().equals(email))) {
            throw new ConflictException("Адрес электронной почты пользователя должен быть уникальным.");
        }
        if (email != null) {
            modifiedUser.setEmail(email);
        }

        return UserMapper.toUserDto(userJpaRepository.save(modifiedUser));
    }

    @Override
    public UserDto getUser(Integer userId) {
        return UserMapper.toUserDto(
                userJpaRepository.findById(userId).orElseThrow(
                        () -> new NotFoundException(
                                String.format("Пользователь с id=%d отсутствует в базе.", userId))));
    }

    @Override
    public List getAllUsers() {
        return userJpaRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        userJpaRepository.deleteById(userId);
    }
}
