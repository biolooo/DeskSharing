package org.example.desksharing.repository;

import org.example.desksharing.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorRepository
        extends JpaRepository<Floor, Long> {

    List<Floor> findByBuildingId(Long buildingId);
}
