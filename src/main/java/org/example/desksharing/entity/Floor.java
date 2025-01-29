package org.example.desksharing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String floorPlanImage;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building; // Verknüpfung mit dem Gebäude
}
