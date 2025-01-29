package org.example.desksharing.repository;

import org.example.desksharing.entity.Role;
import org.example.desksharing.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
