package com.greally2014.ticketmanager.dao;

import com.greally2014.ticketmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User u set u.userName = :userName, u.firstName = :firstName, u.lastName = :lastName, " +
            "u.email = :email where u.userName = :principalUsername")
    void updateProfileDetails(String userName, String firstName, String lastName, String email, String principalUsername);
}
