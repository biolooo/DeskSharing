package org.example.desksharing.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDeskRequest {
    private Long deskId;
    private LocalDate bookedDate;
}
