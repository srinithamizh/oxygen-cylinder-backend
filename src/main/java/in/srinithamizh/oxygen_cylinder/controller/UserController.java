package in.srinithamizh.oxygen_cylinder.controller;

import in.srinithamizh.oxygen_cylinder.entity.User;
import in.srinithamizh.oxygen_cylinder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static in.srinithamizh.oxygen_cylinder.entity.Role.ROLE_USER;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            if (userService.findUserByUsername(user.getUsername()).isPresent()) {
                logger.info("Username {} already taken", user.getUsername());
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Username already taken. Please try again with new one!");
            }

            User savedUser = userService.registration(user, ROLE_USER);
            logger.info("User successfully registered with username: {}", savedUser.getUsername());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(String.format("%s is created successfully!", savedUser.getUsername()));
        } catch (Exception e) {
            logger.error("Failed to register the user with username: {}", user.getUsername());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
