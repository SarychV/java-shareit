package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl service;

    UserDto userDtoIn;
    User userIn;

    @BeforeEach
    void initUsers() {
        userDtoIn = new UserDto();
        userDtoIn.setId(1);
        userDtoIn.setName("UserDtoIn");
        userDtoIn.setEmail("UserDI@mail.co");

        userIn = new User();
        userIn.setId(1);
        userIn.setName("UserDtoIn");
        userIn.setEmail("UserDI@mail.co");
    }

    @Test
    void addUser_whenValidData_thenSuccessResult() {
        Mockito.when(userRepository.save(any(User.class)))
                .then(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto userDtoOut = service.addUser(userDtoIn);

        assertEquals(userDtoIn, userDtoOut);
    }

    @Test
    void addUser_whenNameAbsent_thenThrowException() {
        userDtoIn.setName(null);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addUser(userDtoIn));
        assertEquals("Задайте имя пользователя или адрес электронной почты.", ex.getMessage());
        Mockito.verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_whenEmailAbsent_thenThrowException() {
        userDtoIn.setEmail(null);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addUser(userDtoIn));
        assertEquals("Задайте имя пользователя или адрес электронной почты.", ex.getMessage());
        Mockito.verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUser_whenEmailNotValid_thenThrowException() {
        userDtoIn.setEmail("userDtoIn");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addUser(userDtoIn));
        assertEquals("Неверный формат адреса электронной почты.", ex.getMessage());
        Mockito.verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenValidData_thenSuccessResult() {
        userDtoIn.setName("newName");
        userDtoIn.setEmail("newName@mail.co");
        Mockito.when(userRepository.findById(userDtoIn.getId())).thenReturn(Optional.of(userIn));
        Mockito.when(userRepository.findByEmailContainingIgnoreCase(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any(User.class)))
                .then(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserDto updatedUserDto = service.updateUser(userDtoIn);

        assertEquals(userDtoIn, updatedUserDto);
    }

    @Test
    void updateUser_when_then() {
        userDtoIn.setName("newName");
        userDtoIn.setEmail("newName@mail.co");
        Mockito.when(userRepository.findById(userDtoIn.getId())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.updateUser(userDtoIn));
        Mockito.verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_whenAnotherUserHasTheSameEmail_thenThrowException() {
        userDtoIn.setName("newName");
        userDtoIn.setEmail("newName@mail.co");
        Mockito.when(userRepository.findById(userDtoIn.getId())).thenReturn(Optional.of(userIn));
        Mockito.when(userRepository.findByEmailContainingIgnoreCase(anyString())).thenReturn(Optional.of(new User()));

        ConflictException ex = assertThrows(ConflictException.class, () -> service.updateUser(userDtoIn));
        Mockito.verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUser_whenValidData_thenSuccessResult() {
        int userId = userIn.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userIn));

        UserDto userDto = service.getUser(userId);

        assertEquals(userDtoIn, userDto);
    }

    @Test
    void getUser_whenUserNotFound_thenThrowException() {
        int userId = userIn.getId();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getUser(userId));
    }

        @Test
    void getAllUsers() {
        List<User> list = List.of(userIn, userIn);
        List<UserDto> listDto = List.of(userDtoIn, userDtoIn);
        Mockito.when(userRepository.findAll()).thenReturn(list);

        List<UserDto> listDtoResult = service.getAllUsers();

        assertEquals(listDto, listDtoResult);
    }
}