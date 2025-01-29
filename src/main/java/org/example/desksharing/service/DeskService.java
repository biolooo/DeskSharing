package org.example.desksharing.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.desksharing.entity.Booking;
import org.example.desksharing.entity.Desk;
import org.example.desksharing.entity.User;
import org.example.desksharing.repository.BookingRepository;
import org.example.desksharing.repository.DeskRepository;
import org.example.desksharing.repository.UserRepository;
import org.example.desksharing.request.BookingDTO;
import org.example.desksharing.request.DeskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeskService {

    @Autowired
    private DeskRepository deskRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    public Desk update(Desk desk, Long id) {
        // Prüfen, ob der Tisch existiert
        return deskRepository.findById(id).map(existingDesk -> {
            // Nur Felder aktualisieren, die nicht null sind
            if (desk.getY() != 0) {
                existingDesk.setY(desk.getY());
            }
            if (desk.getX() != 0) {
                existingDesk.setX(desk.getX());
            }
            if (desk.getName() != null && !desk.getName().isEmpty()) {
                existingDesk.setName(desk.getName());
            }
            if (desk.getDescription() != null && !desk.getDescription().isEmpty()) {
                existingDesk.setDescription(desk.getDescription());
            }
            return deskRepository.save(existingDesk);
        }).orElseThrow(() -> new EntityNotFoundException("Desk with id " + id + " not found"));
    }

    public List<LocalDate> getDeskAvailability(Long deskId) {
        return bookingRepository.findBookedDatesByDeskId(deskId);

    }

    public List<BookingDTO> getDeskBookings(Long deskId) {
        // 1) Hole alle Booking-Einträge zu dieser deskId
        List<Booking> bookings = bookingRepository.findByDeskId(deskId);

        // 2) Wandle jeden Booking-Eintrag in ein BookingDTO um
        return bookings.stream()
                .map(booking -> {
                    BookingDTO dto = new BookingDTO();
                    dto.setBookingId(booking.getBookingId());

                    // LocalDate -> String
                    if (booking.getBookedDate() != null) {
                        dto.setBookedDate(booking.getBookedDate());
                    }

                    // Username, wenn vorhanden
                    if (booking.getUser() != null) {
                        dto.setUsername(booking.getUser().getUsername());
                    }

                    // Deskname, wenn vorhanden
                    if (booking.getDesk() != null) {
                        dto.setDeskName(booking.getDesk().getName());
                        System.out.println(booking.getDesk().getName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Desk> getAllDesks() {
        List<Desk> desks = deskRepository.findAll();
        return setAvailability(desks);
    }

    public List<DeskDTO> getByFloor(Long floorId, LocalDate date) {
        List<Desk> desks = deskRepository.findByFloorId(floorId);
        List<DeskDTO> deskDTOs = new ArrayList<>();

        for (Desk desk : desks) {
            DeskDTO deskDTO = new DeskDTO();
            deskDTO.setId(desk.getId());
            deskDTO.setX(desk.getX());
            deskDTO.setY(desk.getY());
            deskDTO.setFloor(desk.getFloor());
            deskDTO.setName(desk.getName());
            deskDTO.setDescription(desk.getDescription());

            // Standardverfügbarkeit
            boolean isAvailable = desk.isAvailable();

            // Buchungsinformationen abrufen
            Optional<Booking> bookingOptional = bookingRepository.findByDeskIdAndBookedDate(desk.getId(), date);
            if (bookingOptional.isPresent()) {
                Booking booking = bookingOptional.get();
                User user = booking.getUser();

                // Setze Buchungsdaten
                deskDTO.setUsername(user.getUsername());
                deskDTO.setProfilePicture(user.getProfilePicture());

                // Markiere Tisch als nicht verfügbar, wenn gebucht
                isAvailable = false;
            }

            // Verfügbarkeit setzen
            deskDTO.setAvailable(isAvailable);

            deskDTOs.add(deskDTO);
        }

        return deskDTOs;
    }

    private List<Desk> setAvailability(List<Desk> desks) {
        for (Desk desk : desks) {
            desk.setAvailable(true);
            List<LocalDate> dates = getDeskAvailability(desk.getId());
            for (LocalDate date : dates) {
                if (date.isEqual(LocalDate.now())) {
                    desk.setAvailable(false);
                    break;
                }
            }
        }
        return desks;
    }
}
