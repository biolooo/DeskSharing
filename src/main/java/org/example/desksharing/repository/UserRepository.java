package org.example.desksharing.repository;

import org.example.desksharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
