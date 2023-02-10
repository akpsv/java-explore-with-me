package ru.akpsv.dto.main.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.create(user).get();
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.get().get();
    }
    @DeleteMapping("/{userId}")
    public void deleteUserById(@RequestParam int userId){
        userService.deleteById(userId);
    }
}

