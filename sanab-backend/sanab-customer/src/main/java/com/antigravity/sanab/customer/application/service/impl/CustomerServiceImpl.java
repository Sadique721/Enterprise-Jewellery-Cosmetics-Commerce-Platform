package com.antigravity.sanab.customer.application.service.impl;

import com.antigravity.sanab.customer.api.dto.request.AddressRequest;
import com.antigravity.sanab.customer.api.dto.response.AddressResponse;
import com.antigravity.sanab.customer.api.dto.response.CustomerProfileResponse;
import com.antigravity.sanab.customer.application.service.CustomerService;
import com.antigravity.sanab.customer.domain.entity.Address;
import com.antigravity.sanab.customer.domain.entity.CustomerProfile;
import com.antigravity.sanab.customer.domain.entity.WishlistItem;
import com.antigravity.sanab.customer.domain.repository.AddressRepository;
import com.antigravity.sanab.customer.domain.repository.CustomerProfileRepository;
import com.antigravity.sanab.customer.domain.repository.WishlistItemRepository;
import com.antigravity.sanab.shared.exception.ErrorCode;
import com.antigravity.sanab.shared.exception.SanabException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    private final WishlistItemRepository wishlistRepository;

    @Override
    public CustomerProfileResponse getProfile(UUID userId) {
        CustomerProfile profile = getOrCreateProfile(userId);
        return mapToProfileResponse(profile);
    }

    @Override
    public AddressResponse addAddress(UUID userId, AddressRequest req) {
        CustomerProfile profile = getOrCreateProfile(userId);

        if (req.defaultAddress()) {
            profile.getAddresses().forEach(a -> a.setDefaultAddress(false));
        }

        Address address = Address.builder()
                .customerProfile(profile)
                .fullName(req.fullName().strip())
                .phone(req.phone().strip())
                .streetAddress(req.streetAddress().strip())
                .apartmentSuite(req.apartmentSuite() != null ? req.apartmentSuite().strip() : null)
                .city(req.city().strip())
                .stateProvince(req.stateProvince().strip())
                .postalCode(req.postalCode().strip())
                .country(req.country().strip())
                .addressType(req.addressType())
                .defaultAddress(req.defaultAddress() || profile.getAddresses().isEmpty())
                .build();

        Address saved = addressRepository.save(address);
        log.info("Added address for userId={}, addressId={}", userId, saved.getId());
        return mapToAddressResponse(saved);
    }

    @Override
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest req) {
        CustomerProfile profile = getOrCreateProfile(userId);
        Address address = addressRepository.findByIdAndCustomerProfileId(addressId, profile.getId())
                .orElseThrow(() -> new SanabException(ErrorCode.ADDRESS_NOT_FOUND, "Address not found"));

        if (req.defaultAddress()) {
            profile.getAddresses().forEach(a -> a.setDefaultAddress(false));
        }

        address.setFullName(req.fullName().strip());
        address.setPhone(req.phone().strip());
        address.setStreetAddress(req.streetAddress().strip());
        address.setApartmentSuite(req.apartmentSuite() != null ? req.apartmentSuite().strip() : null);
        address.setCity(req.city().strip());
        address.setStateProvince(req.stateProvince().strip());
        address.setPostalCode(req.postalCode().strip());
        address.setCountry(req.country().strip());
        address.setAddressType(req.addressType());
        address.setDefaultAddress(req.defaultAddress());

        Address saved = addressRepository.save(address);
        return mapToAddressResponse(saved);
    }

    @Override
    public void deleteAddress(UUID userId, UUID addressId) {
        CustomerProfile profile = getOrCreateProfile(userId);
        Address address = addressRepository.findByIdAndCustomerProfileId(addressId, profile.getId())
                .orElseThrow(() -> new SanabException(ErrorCode.ADDRESS_NOT_FOUND, "Address not found"));

        addressRepository.delete(address);
        log.info("Deleted addressId={} for userId={}", addressId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(UUID userId) {
        CustomerProfile profile = getOrCreateProfile(userId);
        return addressRepository.findByCustomerProfileId(profile.getId()).stream()
                .map(this::mapToAddressResponse)
                .toList();
    }

    @Override
    public void addToWishlist(UUID userId, UUID productId) {
        CustomerProfile profile = getOrCreateProfile(userId);
        if (!wishlistRepository.existsByCustomerProfileIdAndProductId(profile.getId(), productId)) {
            WishlistItem item = WishlistItem.builder()
                    .customerProfile(profile)
                    .productId(productId)
                    .build();
            wishlistRepository.save(item);
            log.info("Added productId={} to wishlist for userId={}", productId, userId);
        }
    }

    @Override
    public void removeFromWishlist(UUID userId, UUID productId) {
        CustomerProfile profile = getOrCreateProfile(userId);
        wishlistRepository.deleteByCustomerProfileIdAndProductId(profile.getId(), productId);
        log.info("Removed productId={} from wishlist for userId={}", productId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getWishlistProductIds(UUID userId) {
        CustomerProfile profile = getOrCreateProfile(userId);
        return wishlistRepository.findByCustomerProfileId(profile.getId()).stream()
                .map(WishlistItem::getProductId)
                .toList();
    }

    private CustomerProfile getOrCreateProfile(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> profileRepository.save(CustomerProfile.builder()
                        .userId(userId)
                        .preferredLanguage("en")
                        .preferredCurrency("INR")
                        .build()));
    }

    private CustomerProfileResponse mapToProfileResponse(CustomerProfile p) {
        List<AddressResponse> addrs = p.getAddresses() == null ? List.of() :
                p.getAddresses().stream().map(this::mapToAddressResponse).toList();
        List<UUID> wishs = p.getWishlistItems() == null ? List.of() :
                p.getWishlistItems().stream().map(WishlistItem::getProductId).toList();

        return new CustomerProfileResponse(
                p.getId(), p.getUserId(), p.getAvatarUrl(), p.getDateOfBirth(),
                p.getGender(), p.getPreferredLanguage(), p.getPreferredCurrency(), addrs, wishs
        );
    }

    private AddressResponse mapToAddressResponse(Address a) {
        return new AddressResponse(
                a.getId(), a.getFullName(), a.getPhone(), a.getStreetAddress(),
                a.getApartmentSuite(), a.getCity(), a.getStateProvince(), a.getPostalCode(),
                a.getCountry(), a.getAddressType(), a.isDefaultAddress()
        );
    }
}
