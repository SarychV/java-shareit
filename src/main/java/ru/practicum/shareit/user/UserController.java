package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        user.hasValidEmailOrThrow();
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody User user, @PathVariable Integer userId) {
        user.hasValidEmailOrThrow();
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}
