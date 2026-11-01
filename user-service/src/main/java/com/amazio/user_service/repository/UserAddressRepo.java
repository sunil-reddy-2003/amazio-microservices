package com.amazio.user_service.repository;

import com.amazio.user_service.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepo
        extends JpaRepository<UserAddress, Integer> {

    List<UserAddress> findByUserId(UUID userId);

    Optional<UserAddress> findByAddressIdAndUserId(
            int addressId,
            UUID userId
    );

    boolean existsByUserIdAndDefaultAddressTrue(UUID userId);
}