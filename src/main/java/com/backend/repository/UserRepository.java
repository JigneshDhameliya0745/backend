package com.backend.repository;

import com.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByUserEmail(String userEmail);

    User findByUserId(int userId);

    List<User> findByIsUserDeleted(Boolean isUserDeleted);

    User findByResetToken(String resetToken);

    User findAllByPasswordSetDate(Date passwordSetDate);

    User findAllByRoleId(int roleId);

}
