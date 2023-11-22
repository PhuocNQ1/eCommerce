package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username);

            if (user == null) {
                log.warn("User with username '{}' not found.", username);
                return ResponseEntity.notFound().build();
            } else {
                log.info("User found with username '{}'.", username);
                return ResponseEntity.ok(user);
            }
        } catch (Exception e) {
            log.error("Error finding user by username '{}'.", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            if (createUserRequest.getUsername() != null && createUserRequest.getPassword() != null) {
                if (!createUserRequest.getConfirmPassword().equals(createUserRequest.getPassword())
                        || createUserRequest.getPassword().length() < 8) {
                    log.error("Error with user password.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                User user = new User();
                user.setUsername(createUserRequest.getUsername());
                user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

                Cart cart = new Cart();
                cartRepository.save(cart);
                user.setCart(cart);

                userRepository.save(user);

                log.info("User created successfully. Username: {}", user.getUsername());
                return ResponseEntity.ok(user);
            } else {
                log.error("Invalid request. Username or password is null.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            log.error("Error creating user.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
