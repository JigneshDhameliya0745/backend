package com.backend.service;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> addUser(User user) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User users = userRepository.findUserByUserEmail(username);

        boolean flag = true;
        if (users.getUserId() == user.getUserId() || users.getRole().getRoleName().equals("ADMIN")) {
            int uId = user.getUserId();
            if (uId != 0) {
                User existingUser = userRepository.findByUserId(uId);

                if (user.getFirstName() == null) {
                    if (existingUser.getFirstName() != null) {
                        user.setFirstName(existingUser.getFirstName());
                    }
                }

                if (user.getLastName() == null) {
                    if (existingUser.getLastName() != null) {
                        user.setLastName(existingUser.getLastName());
                    }
                }

                if (user.getAddress() == null) {
                    if (existingUser.getAddress() != null) {
                        user.setAddress(existingUser.getAddress());
                    }
                }

                if (user.getContactNo() == null) {
                    if (existingUser.getContactNo() != null) {
                        user.setContactNo(existingUser.getContactNo());
                    }
                }

                user.setCreatedDate(existingUser.getCreatedDate());

                user.setPasswordSetDate(existingUser.getPasswordSetDate());

                user.setUpdatedDate(new Date());

                if (user.getIsUserActive() == null) {
                    if (existingUser.getIsUserActive() != null) {
                        user.setIsUserActive(existingUser.getIsUserActive());
                    }
                }

                if (user.getIsUserDeleted() == null) {
                    if (existingUser.getIsUserDeleted() != null) {
                        user.setIsUserDeleted(existingUser.getIsUserDeleted());
                    }
                }

                if (user.getRoleId() == 0) {
                    if (existingUser.getRoleId() != 0) {
                        user.setRoleId(existingUser.getRoleId());
                    }
                }

                if (user.getUserEmail() == null) {
                    if (existingUser.getUserEmail() != null) {
                        user.setUserEmail(existingUser.getUserEmail());
                    }
                }

                if (user.getUserPassword() == null) {
                    if (existingUser.getUserPassword() != null) {
                        user.setUserPassword(existingUser.getUserPassword());
                    }
                }

                updateUser(user, uId);
                flag = false;
                return ResponseEntity.ok("User Updated Successfully!");
            }
        }

        if (users.getRole().getRoleName().equals("ADMIN")) {
            if (flag) {
                if (user.getUserId() == 0) {
                    user.setCreatedDate(new Date());
                    user.setResetToken(UUID.randomUUID().toString());
                    user.setIsUserDeleted(false);
                }
                userRepository.save(user);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.badRequest().body("Enter Valid User Details!");
            }
        }
        return ResponseEntity.ok(user);
    }

    public ResponseEntity getUser() {
        List<User> userList = userRepository.findByIsUserDeleted(false);
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    public ResponseEntity getAllUser() {
        List<User> userList = userRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    public User updateUser(User user, int userId) {
        user.setUserId(userId);
        return userRepository.save(user);
    }

    public ResponseEntity<?> deleteUser(int userId) {
        User user = userRepository.findByUserId(userId);
        user.setUserId(userId);
        user.setIsUserDeleted(true);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("User Deleted Successfully!");
    }

    public ResponseEntity getAdminUser() {
        List<User> userList = (List<User>) userRepository.findAllByRoleId(1);
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

}
