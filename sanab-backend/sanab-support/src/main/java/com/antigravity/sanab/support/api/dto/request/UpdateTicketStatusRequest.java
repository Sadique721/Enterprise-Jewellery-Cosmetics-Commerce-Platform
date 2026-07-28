package com.antigravity.sanab.support.api.dto.request;

import com.antigravity.sanab.support.domain.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull(message = "Status is required")
        TicketStatus status
) {}
