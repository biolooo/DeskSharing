package org.example.desksharing.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long bookingId;
    private LocalDate bookedDate;  // oder LocalDate, je nach Bedarf
    private String username;
    private String deskName;
}
