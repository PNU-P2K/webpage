package com.example.p2k.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    @Modifying
    @Query("update User u SET u.password = :password where u.id = :userId")
    void resetPassword(@Param("userId") Long userId, @Param("password") String password);

    @Modifying
    @Query("update User u SET u.email = :email, u.name = :name where u.id = :userId")
    void update(@Param("userId") Long userId, @Param("email") String email, @Param("name") String name);
}
