package in.srinithamizh.oxygen_cylinder.controller;

import in.srinithamizh.oxygen_cylinder.entity.User;
import in.srinithamizh.oxygen_cylinder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static in.srinithamizh.oxygen_cylinder.entity.Role.ROLE_USER;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        try {
            if (userService.findUserByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Username already taken. Please try again with new one!");
            }

            User savedUser = userService.registration(user, ROLE_USER);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser.getUsername() + "is created successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
