package com.amazio.user_service.service;

import com.amazio.user_service.dto.AddressRequestDto;
import com.amazio.user_service.dto.RegisterUserDto;
import com.amazio.user_service.dto.UserAddressDto;
import com.amazio.user_service.dto.UserRequestDto;
import com.amazio.user_service.entity.User;
import com.amazio.user_service.entity.UserAddress;
import com.amazio.user_service.enums.UserEventType;
import com.amazio.user_service.enums.UserRole;
import com.amazio.user_service.exception.AddressNotFoundException;
import com.amazio.user_service.exception.UserAlreadyExistsException;
import com.amazio.user_service.exception.UserNotFoundException;
import com.amazio.user_service.kafka.UserEventProducer;
import com.amazio.user_service.repository.UserAddressRepo;
import com.amazio.user_service.repository.UserRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final UserAddressRepo userAddressRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    @Transactional
    public User registerUser(RegisterUserDto registerUser) {
    log.info("inside registerUser : "+registerUser);
        if (userRepo.existsByEmail(registerUser.email())) {
            throw new UserAlreadyExistsException(
                    "Email is already registered"
            );
        }

        if (userRepo.existsByMobile(registerUser.mobile())) {
            throw new UserAlreadyExistsException(
                    "Mobile number is already registered"
            );
        }

        User user = new User();

        user.setFName(registerUser.fName());
        user.setLName(registerUser.lName());
        user.setEmail(registerUser.email());
        user.setPassword(passwordEncoder.encode(registerUser.password()));
        user.setMobile(registerUser.mobile());
        user.setUserRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser= userRepo.save(user);

        userEventProducer.publishUserEvent(
                savedUser.getUserId(),
                savedUser.getFName(),
                savedUser.getLName(),
                savedUser.getEmail(),
                UserEventType.USER_SIGNUP.toString(),
                "User registered successfully"
        );

        return savedUser;
    }


    public User getUserById(UUID userId) {

        return userRepo.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found: " + userId
                        )
                );
    }


    public User getUserByEmail(String email) {

        return userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with email: " + email
                        )
                );
    }


    @Transactional
    public User updateUser(UUID userId, UserRequestDto userDetails) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: " + userId
                        )
                );
//
//        if (!user.getEmail().equalsIgnoreCase(userDetails.getEmail())
//                && userRepo.existsByEmail(userDetails.getEmail())) {
//
//            throw new UserAlreadyExistsException(
//                    "Email is already registered"
//            );
//        }
//
//        if (user.getMobile() != userDetails.getMobile()
//                && userRepo.existsByMobile(userDetails.getMobile())) {
//
//            throw new UserAlreadyExistsException(
//                    "Mobile number is already registered"
//            );
//        }

        user.setFName(userDetails.getFName());
        user.setLName(userDetails.getLName());
        user.setEmail(userDetails.getEmail());
        user.setMobile(userDetails.getMobile());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepo.save(user);

        userEventProducer.publishUserEvent(
                updatedUser.getUserId(),
                updatedUser.getFName(),
                updatedUser.getLName(),
                updatedUser.getEmail(),
                UserEventType.USER_UPDATED.toString(),
                "User profile updated successfully"
        );

        return updatedUser;
    }


    @Transactional
    public UserAddress saveAddress(
            UUID userId,
            UserAddressDto addressDto
    ) {

        userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: " + userId
                        )
                );

        UserAddress address = new UserAddress();

        address.setUserId(userId);

        address.setPincode(addressDto.getPincode());
        address.setArea(addressDto.getArea());
        address.setFlat(addressDto.getFlat());
        address.setLandmark(addressDto.getLandmark());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setAddressType(addressDto.getAddressType());
        address.setName(addressDto.getName());
        address.setMobile(addressDto.getMobile());

        boolean hasDefaultAddress =
                userAddressRepo.existsByUserIdAndDefaultAddressTrue(userId);

        // The first address becomes the default address.
        boolean makeDefault =
                !hasDefaultAddress || addressDto.isDefaultAddress();

        if (makeDefault) {
            List<UserAddress> existingAddresses =
                    userAddressRepo.findByUserId(userId);

            existingAddresses.forEach(existing ->
                    existing.setDefaultAddress(false)
            );

            userAddressRepo.saveAll(existingAddresses);
        }

        address.setDefaultAddress(makeDefault);

        LocalDateTime now = LocalDateTime.now();

        address.setCreatedAt(now);
        address.setUpdatedAt(now);

        return userAddressRepo.save(address);
    }


    public List<UserAddress> getAddresses(UUID userId) {

        userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: " + userId
                        )
                );

        List<UserAddress> addresses =
                userAddressRepo.findByUserId(userId);

        if (addresses.isEmpty()) {
            throw new AddressNotFoundException(
                    "No saved addresses found for this user"
            );
        }

        return addresses;
    }


    @Transactional
    public UserAddress updateAddress(
            UUID userId,
            int addressId,
            AddressRequestDto addressRequest
    ) {

        userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: " + userId
                        )
                );

        UserAddress address = userAddressRepo
                .findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new AddressNotFoundException(
                                "Address not found for this user"
                        )
                );

        // Prevent removing the only default address
        if (address.isDefaultAddress() && !addressRequest.isDefaultAddress()) {

            boolean anotherDefaultExists = userAddressRepo
                    .findByUserId(userId)
                    .stream()
                    .anyMatch(existing ->
                            existing.isDefaultAddress()
                                    && existing.getAddressId() != addressId
                    );

            if (!anotherDefaultExists) {
                throw new IllegalArgumentException(
                        "At least one default address is required"
                );
            }
        }

        // If this address is being set as default,
        // make all other addresses non-default
        if (addressRequest.isDefaultAddress()) {

            List<UserAddress> existingAddresses =
                    userAddressRepo.findByUserId(userId);

            existingAddresses.forEach(existing ->
                    existing.setDefaultAddress(false)
            );

            userAddressRepo.saveAll(existingAddresses);
        }

        // Update address details
        address.setPincode(addressRequest.getPincode());
        address.setArea(addressRequest.getArea());
        address.setFlat(addressRequest.getFlat());
        address.setLandmark(addressRequest.getLandmark());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setAddressType(addressRequest.getAddressType());
        address.setName(addressRequest.getName());
        address.setMobile(addressRequest.getMobile());
        address.setDefaultAddress(addressRequest.isDefaultAddress());
        address.setUpdatedAt(LocalDateTime.now());

        return userAddressRepo.save(address);
    }

    @Transactional
    public void deleteAddress(UUID userId, int addressId) {

        UserAddress address = userAddressRepo
                .findByAddressIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new AddressNotFoundException(
                                "Address not found for this user"
                        )
                );

        List<UserAddress> existingAddresses =
                userAddressRepo.findByUserId(userId);

        // If deleting the default address
        if (address.isDefaultAddress()) {

            // Prevent deleting the only existing address
            if (existingAddresses.size() == 1) {
                throw new IllegalArgumentException(
                        "Cannot delete the only default address"
                );
            }

            // Make another address the default
            existingAddresses.stream()
                    .filter(existing ->
                            existing.getAddressId() != addressId
                    )
                    .findFirst()
                    .ifPresent(nextDefault -> {
                        nextDefault.setDefaultAddress(true);
                        userAddressRepo.save(nextDefault);
                    });
        }

        userAddressRepo.delete(address);
    }


}