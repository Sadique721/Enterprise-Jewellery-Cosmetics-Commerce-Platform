package com.antigravity.sanab.support.domain.repository;

import com.antigravity.sanab.support.domain.entity.SupportTicket;
import com.antigravity.sanab.support.domain.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    Optional<SupportTicket> findByTicketNumber(String ticketNumber);

    Page<SupportTicket> findByUserId(UUID userId, Pageable pageable);

    Page<SupportTicket> findByStatus(TicketStatus status, Pageable pageable);
}
