package com.antigravity.sanab.customer.api.controller;

import com.antigravity.sanab.customer.domain.entity.Address;
import com.antigravity.sanab.customer.domain.repository.AddressRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Enterprise Address Controller providing user address management and default toggles.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Customer Addresses", description = "Endpoints for saved customer shipping & billing addresses")
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping
    @Operation(summary = "Get all saved addresses for a customer profile")
    public ResponseEntity<List<Address>> getUserAddresses(@RequestParam UUID customerProfileId) {
        List<Address> list = addressRepository.findByCustomerProfileId(customerProfileId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/save")
    @Transactional
    @Operation(summary = "Save or update a customer address with optional default flag")
    public ResponseEntity<Address> saveAddress(@RequestBody Address address,
                                               @RequestParam(required = false) Boolean isDefault) {
        if (Boolean.TRUE.equals(isDefault)) {
            List<Address> existing = addressRepository.findByCustomerProfileId(address.getCustomerProfile().getId());
            for (Address addr : existing) {
                if (Boolean.TRUE.equals(addr.isDefaultAddress())) {
                    addr.setDefaultAddress(false);
                    addressRepository.save(addr);
                }
            }
            address.setDefaultAddress(true);
        }
        Address saved = addressRepository.save(address);
        log.info("Saved customer address id={} for profile={}", saved.getId(), address.getCustomerProfile().getId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/set-default")
    @Transactional
    @Operation(summary = "Set a specific address as default for customer profile")
    public ResponseEntity<String> setDefaultAddress(@RequestParam UUID addressId,
                                                     @RequestParam UUID customerProfileId) {
        Optional<Address> optionalAddress = addressRepository.findByIdAndCustomerProfileId(addressId, customerProfileId);
        if (optionalAddress.isPresent()) {
            List<Address> userAddresses = addressRepository.findByCustomerProfileId(customerProfileId);
            for (Address addr : userAddresses) {
                if (Boolean.TRUE.equals(addr.isDefaultAddress())) {
                    addr.setDefaultAddress(false);
                    addressRepository.save(addr);
                }
            }
            Address target = optionalAddress.get();
            target.setDefaultAddress(true);
            addressRepository.save(target);
            log.info("Set address id={} as default for customer profile={}", addressId, customerProfileId);
            return ResponseEntity.ok("Default address updated successfully");
        }
        return ResponseEntity.badRequest().body("Address not found or unauthorized");
    }

    @DeleteMapping("/{addressId}")
    @Transactional
    @Operation(summary = "Delete an address for customer profile")
    public ResponseEntity<String> deleteAddress(@PathVariable UUID addressId,
                                                @RequestParam UUID customerProfileId) {
        Optional<Address> optionalAddress = addressRepository.findByIdAndCustomerProfileId(addressId, customerProfileId);
        if (optionalAddress.isPresent()) {
            Address address = optionalAddress.get();
            if (Boolean.TRUE.equals(address.isDefaultAddress())) {
                List<Address> otherAddresses = addressRepository.findByCustomerProfileId(customerProfileId);
                for (Address addr : otherAddresses) {
                    if (!addr.getId().equals(addressId)) {
                        addr.setDefaultAddress(true);
                        addressRepository.save(addr);
                        break;
                    }
                }
            }
            addressRepository.deleteById(addressId);
            log.info("Deleted address id={} for profile={}", addressId, customerProfileId);
            return ResponseEntity.ok("Address deleted successfully");
        }
        return ResponseEntity.badRequest().body("Address not found or unauthorized");
    }
}
