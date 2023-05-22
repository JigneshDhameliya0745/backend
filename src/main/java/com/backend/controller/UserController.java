package com.backend.controller;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/backend")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private String TAG_NAME = "UserController";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/addUser")
    public ResponseEntity<?> addUser(@RequestBody User user) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside addProduct : Product :: " + user);
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/getUser")
    public ResponseEntity<?> getUser() throws Exception {
        LOGGER.info(TAG_NAME + " :: inside getUser : User :: " );
        return ResponseEntity.ok(userService.getUser());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/getAllUser")
    public ResponseEntity<?> getAllUser() throws Exception {
        LOGGER.info(TAG_NAME + " :: inside getAllUser : User :: " );
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @PutMapping(value = "/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside updateUser : User :: " + user);
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestBody User user) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside deleteUser : User :: " + user);
        return ResponseEntity.ok(userService.deleteUser(user.getUserId()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/getAdminUser")
    public ResponseEntity<?> getAdminUser() throws Exception {
        LOGGER.info(TAG_NAME + " :: inside getAdminUser : AdminUser :: " );
        return ResponseEntity.ok(userService.getAdminUser());
    }
}
