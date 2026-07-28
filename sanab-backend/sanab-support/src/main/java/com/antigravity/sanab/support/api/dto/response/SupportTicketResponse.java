package com.antigravity.sanab.support.api.dto.response;

import com.antigravity.sanab.support.domain.enums.TicketPriority;
import com.antigravity.sanab.support.domain.enums.TicketStatus;

import java.time.Instant;
import java.util.UUID;

public record SupportTicketResponse(
        UUID id,
        String ticketNumber,
        UUID userId,
        UUID orderId,
        String subject,
        String description,
        TicketStatus status,
        TicketPriority priority,
        Instant createdAt
) {}
