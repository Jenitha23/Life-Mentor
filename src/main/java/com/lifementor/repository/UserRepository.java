package com.lifementor.repository;

import com.lifementor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.accountLocked = false")
    Optional<User> findByEmailAndNotLocked(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since")
    long countNewUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT u FROM User u WHERE u.accountLocked = false")
    List<User> findAllUnlockedUsers();
}
