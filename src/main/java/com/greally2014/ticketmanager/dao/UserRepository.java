package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsUserByUsername(String username);

    @Modifying
    @Query("update User u set u.username = :username, " +
            "u.firstName = :firstName, u.lastName = :lastName, " +
            "u.email = :email " +
            "where u.username = :principalUsername")
    void updateProfileDetails(String username, String firstName, String lastName, String email, String principalUsername);
}
