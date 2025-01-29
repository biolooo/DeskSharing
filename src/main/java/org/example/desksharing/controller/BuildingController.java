package org.example.desksharing.controller;

import org.example.desksharing.entity.Building;
import org.example.desksharing.entity.Desk;
import org.example.desksharing.entity.Floor;
import org.example.desksharing.repository.BookingRepository;
import org.example.desksharing.repository.BuildingRepository;
import org.example.desksharing.repository.DeskRepository;
import org.example.desksharing.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
public class BuildingController {

    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private DeskRepository deskRepository;
    @Autowired
    private FloorRepository floorRepository;

    @GetMapping
    public ResponseEntity<List<Building>> getAllBuildings() {
        return ResponseEntity.ok(buildingRepository.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Long id) {
        Building building = buildingRepository.findById(id).orElse(null);
        return ResponseEntity.ok(building);
    }

    @PostMapping
    public ResponseEntity<Building> createBuilding(@RequestBody Building building) {
        return ResponseEntity.ok(buildingRepository.save(building));
    }
    @PutMapping("/{buildingId}")
    public ResponseEntity<Building> updateBuilding(@PathVariable Long buildingId, @RequestBody Building updatedBuilding) {
        return buildingRepository.findById(buildingId)
                .map(existingBuilding -> {
                    existingBuilding.setName(updatedBuilding.getName());
                    existingBuilding.setAddress(updatedBuilding.getAddress());
                    buildingRepository.save(existingBuilding);
                    return ResponseEntity.ok(existingBuilding);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long buildingId) {
        if (buildingRepository.existsById(buildingId)) {
            // Schritt 1: Stockwerke des Gebäudes abrufen
            List<Floor> floors = floorRepository.findByBuildingId(buildingId);
            for (Floor floor : floors) {
                // Schritt 2: Tische des Stockwerks abrufen
                List<Desk> desks = deskRepository.findByFloorId(floor.getId());
                for (Desk desk : desks) {
                    // Schritt 3: Buchungen des Tisches löschen
                    bookingRepository.deleteByDeskId(desk.getId());
                    // Schritt 4: Tisch löschen
                    deskRepository.delete(desk);
                }
                // Schritt 5: Stockwerk löschen
                floorRepository.delete(floor);
            }
            // Schritt 6: Gebäude löschen
            buildingRepository.deleteById(buildingId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
