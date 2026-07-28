package com.antigravity.sanab.support.application.service.impl;

import com.antigravity.sanab.shared.api.response.PagedResponse;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import com.antigravity.sanab.support.api.dto.request.CreateTicketRequest;
import com.antigravity.sanab.support.api.dto.request.UpdateTicketStatusRequest;
import com.antigravity.sanab.support.api.dto.response.SupportTicketResponse;
import com.antigravity.sanab.support.application.service.SupportTicketService;
import com.antigravity.sanab.support.domain.entity.SupportTicket;
import com.antigravity.sanab.support.domain.enums.TicketPriority;
import com.antigravity.sanab.support.domain.enums.TicketStatus;
import com.antigravity.sanab.support.domain.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository ticketRepository;

    @Override
    public SupportTicketResponse createTicket(UUID userId, CreateTicketRequest req) {
        String ticketNo = "TICK-" + System.currentTimeMillis();

        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber(ticketNo)
                .userId(userId)
                .orderId(req.orderId())
                .subject(req.subject().strip())
                .description(req.description().strip())
                .status(TicketStatus.OPEN)
                .priority(req.priority() != null ? req.priority() : TicketPriority.MEDIUM)
                .build();

        SupportTicket saved = ticketRepository.save(ticket);
        log.info("Created support ticket: ticketNo={}, userId={}", ticketNo, userId);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupportTicketResponse> getUserTickets(UUID userId, Pageable pageable) {
        Page<SupportTicketResponse> page = ticketRepository.findByUserId(userId, pageable).map(this::mapToResponse);
        return PagedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketByNumber(UUID userId, String ticketNumber) {
        SupportTicket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new SanabException(ErrorCode.RESOURCE_NOT_FOUND, "Support ticket not found"));

        if (!ticket.getUserId().equals(userId)) {
            throw new SanabException(ErrorCode.ACCESS_DENIED, "Access denied to support ticket");
        }

        return mapToResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupportTicketResponse> getAllTicketsAdmin(TicketStatus status, Pageable pageable) {
        Page<SupportTicket> page = status != null ?
                ticketRepository.findByStatus(status, pageable) :
                ticketRepository.findAll(pageable);
        return PagedResponse.of(page.map(this::mapToResponse));
    }

    @Override
    public SupportTicketResponse updateTicketStatusAdmin(UUID ticketId, UpdateTicketStatusRequest req) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new SanabException(ErrorCode.RESOURCE_NOT_FOUND, "Support ticket not found"));

        ticket.setStatus(req.status());
        SupportTicket saved = ticketRepository.save(ticket);
        log.info("Updated ticket status: ticketId={}, status={}", ticketId, req.status());
        return mapToResponse(saved);
    }

    private SupportTicketResponse mapToResponse(SupportTicket t) {
        return new SupportTicketResponse(
                t.getId(), t.getTicketNumber(), t.getUserId(), t.getOrderId(),
                t.getSubject(), t.getDescription(), t.getStatus(), t.getPriority(),
                t.getCreatedAt()
        );
    }
}
