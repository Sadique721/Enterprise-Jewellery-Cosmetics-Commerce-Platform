package com.antigravity.sanab.support.api.dto.request;

import com.antigravity.sanab.support.domain.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTicketRequest(
        UUID orderId,

        @NotBlank(message = "Subject is required")
        @Size(max = 200)
        String subject,

        @NotBlank(message = "Description is required")
        String description,

        TicketPriority priority
) {}
