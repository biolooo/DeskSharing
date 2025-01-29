package org.example.desksharing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data // Generiert Getter, Setter, equals, hashCode, und toString
@NoArgsConstructor // Generiert einen No-Args-Konstruktor
@AllArgsConstructor // Generiert einen All-Args-Konstruktor
@Builder // Erlaubt das Erstellen von Objekten mit der Builder-Pattern
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk; // Verknüpfung zur Desk-Entität

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Verknüpfung zur User-Entität

    private LocalDate bookedDate;
}
