package org.example.desksharing.repository;

import org.example.desksharing.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository
        extends JpaRepository<Building, Long> {
}
