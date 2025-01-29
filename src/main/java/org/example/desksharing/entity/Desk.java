package org.example.desksharing.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Desk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor; // Verknüpfung mit dem Stockwerk

    //Felder für die Koordinaten
    private double x;
    private double y;
    public boolean available;
}
