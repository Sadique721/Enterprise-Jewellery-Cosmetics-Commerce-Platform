package com.antigravity.sanab.customer.domain.repository;

import com.antigravity.sanab.customer.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByCustomerProfileId(UUID customerProfileId);

    Optional<Address> findByIdAndCustomerProfileId(UUID id, UUID customerProfileId);
}
