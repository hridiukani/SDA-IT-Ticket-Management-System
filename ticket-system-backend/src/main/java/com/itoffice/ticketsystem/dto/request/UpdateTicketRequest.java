package com.itoffice.ticketsystem.dto.request;

import com.itoffice.ticketsystem.model.enums.TicketPriority;
import com.itoffice.ticketsystem.model.enums.TicketStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTicketRequest {
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private TicketStatus status;

    private TicketPriority priority;

    private UUID assignedToId;
}
