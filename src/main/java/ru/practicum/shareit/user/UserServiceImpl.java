package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
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
            throw new ValidationException("Имя пользователя и адрес его электронной почты должны быть заданы.");
        }

        User user = UserMapper.toUser(userDto);

        user.hasValidEmailOrThrow();
        if (userRepository.isNotUniqueEmail(user.getEmail())) {
            throw new ConflictException("Адрес электронной почты пользователя должен быть уникальным.");
        }

        return UserMapper.toUserDto(userRepository.addUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.hasValidEmailOrThrow();

        User modifiedUser = userRepository.getUser(user.getId());

        String name = user.getName();
        if (name != null) {
            modifiedUser.setName(name);
        }

        String email = user.getEmail();
        // В хранилище может быть такой же адрес электронной почты только у самого объекта.
        // Проверка сработает если в хранилище есть такой адрес электронной почты и он не у этого объекта.
        if (userRepository.isNotUniqueEmail(email) && (!modifiedUser.getEmail().equals(email))) {
            throw new ConflictException("Адрес электронной почты пользователя должен быть уникальным.");
        }
        if (email != null) {
            modifiedUser.setEmail(email);
        }

        return UserMapper.toUserDto(userRepository.updateUser(modifiedUser));
    }

    @Override
    public UserDto getUser(Integer userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public List getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }
}
