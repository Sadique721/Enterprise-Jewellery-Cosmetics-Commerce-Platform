package com.antigravity.sanab.support.api.controller;

import com.antigravity.sanab.shared.api.response.ApiResponse;
import com.antigravity.sanab.shared.api.response.PagedResponse;
import com.antigravity.sanab.support.api.dto.request.CreateTicketRequest;
import com.antigravity.sanab.support.api.dto.request.UpdateTicketStatusRequest;
import com.antigravity.sanab.support.api.dto.response.SupportTicketResponse;
import com.antigravity.sanab.support.application.service.SupportTicketService;
import com.antigravity.sanab.support.domain.enums.TicketStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
@Tag(name = "Customer Support", description = "Help desk, customer support ticket creation, and status tracking")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a customer support ticket")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> createTicket(
            @AuthenticationPrincipal String userIdStr,
            @Valid @RequestBody CreateTicketRequest request) {
        UUID userId = UUID.fromString(userIdStr);
        SupportTicketResponse response = supportTicketService.createTicket(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Support ticket created successfully"));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Get current user's support tickets")
    public ResponseEntity<ApiResponse<PagedResponse<SupportTicketResponse>>> getUserTickets(
            @AuthenticationPrincipal String userIdStr,
            @PageableDefault(size = 10) Pageable pageable) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(supportTicketService.getUserTickets(userId, pageable)));
    }

    @GetMapping("/tickets/{ticketNumber}")
    @Operation(summary = "Get support ticket details by ticket number")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> getTicketByNumber(
            @AuthenticationPrincipal String userIdStr,
            @PathVariable String ticketNumber) {
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(ApiResponse.success(supportTicketService.getTicketByNumber(userId, ticketNumber)));
    }

    @GetMapping("/admin/tickets")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('SUPPORT')")
    @Operation(summary = "Admin: List all support tickets")
    public ResponseEntity<ApiResponse<PagedResponse<SupportTicketResponse>>> getAllTicketsAdmin(
            @RequestParam(required = false) TicketStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(supportTicketService.getAllTicketsAdmin(status, pageable)));
    }

    @PatchMapping("/admin/tickets/{ticketId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('SUPPORT')")
    @Operation(summary = "Admin: Update support ticket status")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> updateTicketStatusAdmin(
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        SupportTicketResponse response = supportTicketService.updateTicketStatusAdmin(ticketId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Ticket status updated"));
    }
}
