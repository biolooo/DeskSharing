package org.example.desksharing.request;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.desksharing.entity.Floor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeskDTO {
    private Long id;
    private boolean available;
    private String username;
    private byte[] profilePicture;
    private String name;
    private String description;
    private Floor floor; // Verknüpfung mit dem Stockwerk
    private double x;
    private double y;
}
