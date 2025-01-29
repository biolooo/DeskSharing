package org.example.desksharing.repository;

import org.example.desksharing.entity.Booking;
import org.example.desksharing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b.bookedDate FROM Booking b WHERE b.desk.id = :deskId")
    List<LocalDate> findBookedDatesByDeskId(@Param("deskId") Long deskId);

    List<Booking> findByDeskId(Long id);

    List<Booking> findByUser(User user);

    @Query("SELECT b FROM Booking b WHERE b.desk.id = :deskId AND b.bookedDate = :date")
    Optional<Booking> findByDeskIdAndBookedDate(@Param("deskId") Long deskId, @Param("date") LocalDate date);

    void deleteByDeskId(Long id);
}
