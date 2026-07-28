package com.antigravity.sanab.support.application.service;

import com.antigravity.sanab.shared.api.response.PagedResponse;
import com.antigravity.sanab.support.api.dto.request.CreateTicketRequest;
import com.antigravity.sanab.support.api.dto.request.UpdateTicketStatusRequest;
import com.antigravity.sanab.support.api.dto.response.SupportTicketResponse;
import com.antigravity.sanab.support.domain.enums.TicketStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SupportTicketService {

    SupportTicketResponse createTicket(UUID userId, CreateTicketRequest request);

    PagedResponse<SupportTicketResponse> getUserTickets(UUID userId, Pageable pageable);

    SupportTicketResponse getTicketByNumber(UUID userId, String ticketNumber);

    PagedResponse<SupportTicketResponse> getAllTicketsAdmin(TicketStatus status, Pageable pageable);

    SupportTicketResponse updateTicketStatusAdmin(UUID ticketId, UpdateTicketStatusRequest request);
}
