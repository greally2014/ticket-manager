package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.verificationCode.code = ?1")
    User findByVerificationCode(String code);

    @Query("SELECT u FROM User u WHERE u.resetPasswordCode.code = ?1")
    User findByResetPasswordCode(String code);

    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    @Modifying
    void updateFailedAttempts(int failAttempts, String email);
}
