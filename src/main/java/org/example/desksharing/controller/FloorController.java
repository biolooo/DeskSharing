package org.example.desksharing.controller;

import org.example.desksharing.entity.Desk;
import org.example.desksharing.entity.Floor;
import org.example.desksharing.repository.BookingRepository;
import org.example.desksharing.repository.DeskRepository;
import org.example.desksharing.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
public class FloorController {

    @Autowired
    private FloorRepository floorRepository;
    @Autowired
    private DeskRepository deskRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Floor>> getAllFloors() {
        return ResponseEntity.ok(floorRepository.findAll());
    }

    @GetMapping
    public ResponseEntity<List<Floor>> getFloorsByBuilding(@RequestParam Long buildingId) {
        // Filtert nur die Stockwerke des angegebenen Gebäudes
        List<Floor> floors = floorRepository.findByBuildingId(buildingId);
        return ResponseEntity.ok(floors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Floor> getFloorById(@PathVariable Long id) {
        if (floorRepository.existsById(id)) {
            return new ResponseEntity<>(floorRepository.findById(id).get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Floor> createFloor(@RequestBody Floor floor) {
        System.out.println(floor.toString());
        return ResponseEntity.ok(floorRepository.save(floor));
    }
    @PutMapping("/{floorId}")
    public ResponseEntity<Floor> updateFloor(@PathVariable Long floorId, @RequestBody Floor updatedFloor) {
        // Prüfen, ob das Stockwerk existiert
        if (!floorRepository.existsById(floorId)) {
            return ResponseEntity.notFound().build();
        }

        // Aktuelles Stockwerk aus der Datenbank abrufen
        Floor existingFloor = floorRepository.findById(floorId).get();

        // Aktualisierung der Felder
        existingFloor.setName(updatedFloor.getName()); // Beispiel: Name aktualisieren
        existingFloor.setBuilding(updatedFloor.getBuilding()); // Beispiel: Gebäude zuweisen

        // Geändertes Objekt speichern
        Floor savedFloor = floorRepository.save(existingFloor);

        // Aktualisiertes Stockwerk zurückgeben
        return ResponseEntity.ok(savedFloor);
    }
    @DeleteMapping("/{floorId}")
    public ResponseEntity<Void> deleteFloor(@PathVariable Long floorId) {
        if (floorRepository.existsById(floorId)) {
            // Schritt 1: Tische des Stockwerks abrufen
            List<Desk> desks = deskRepository.findByFloorId(floorId);
            for (Desk desk : desks) {
                // Schritt 2: Buchungen des Tisches löschen
                bookingRepository.deleteByDeskId(desk.getId());
                // Schritt 3: Tisch löschen
                deskRepository.delete(desk);
            }
            // Schritt 4: Stockwerk löschen
            floorRepository.deleteById(floorId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
