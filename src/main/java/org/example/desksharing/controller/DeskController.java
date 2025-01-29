package org.example.desksharing.controller;

import org.example.desksharing.entity.Booking;
import org.example.desksharing.entity.Desk;
import org.example.desksharing.repository.BookingRepository;
import org.example.desksharing.repository.DeskRepository;
import org.example.desksharing.request.BookingDTO;
import org.example.desksharing.request.DeskDTO;
import org.example.desksharing.service.DeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/desks")
public class DeskController {

    @Autowired
    private DeskRepository deskRepository;
    @Autowired
    DeskService deskService;
    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Desk>> getAllDesks() {
        return ResponseEntity.ok(deskService.getAllDesks());
    }
    @GetMapping("/{deskId}/bookings")
    public ResponseEntity<List<BookingDTO>> getDeskBookings(@PathVariable Long deskId) {
        return ResponseEntity.ok(deskService.getDeskBookings(deskId));
    }

    @GetMapping
    public ResponseEntity<List<DeskDTO>> getDesksByFloor(@RequestParam Long floorId, LocalDate date) {
        LocalDate effectiveDate = date != null ? date : LocalDate.now();
        return new ResponseEntity<>(deskService.getByFloor(floorId, effectiveDate), HttpStatus.OK);
    }
    @GetMapping("/{deskId}/availability")
    public ResponseEntity<List<LocalDate>> getDeskAvailability(@PathVariable Long deskId) {
        return new ResponseEntity<>(deskService.getDeskAvailability(deskId), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Desk> createDesk(@RequestBody Desk desk) {
        desk.setAvailable(true);
        return ResponseEntity.ok(deskRepository.save(desk));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Desk> updateDesk(@PathVariable Long id, @RequestBody Desk desk) {
        return new ResponseEntity<>(deskService.update(desk, id), HttpStatus.OK);
    }

    @DeleteMapping("/{deskId}")
    public ResponseEntity<Void> deleteDesk(@PathVariable Long deskId) {
        if (deskRepository.existsById(deskId)) {
            // Schritt 1: Buchungen des Tisches löschen
            bookingRepository.deleteByDeskId(deskId);
            // Schritt 2: Tisch löschen
            deskRepository.deleteById(deskId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }



}
