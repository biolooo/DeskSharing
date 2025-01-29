package org.example.desksharing.repository;

import org.example.desksharing.entity.Desk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeskRepository
        extends JpaRepository<Desk, Long> {
    List<Desk> findByFloorId(Long floorId);
}
