package com.renthouse.service;

import com.renthouse.dto.FloorDto;
import com.renthouse.dto.PaymentDto;
import com.renthouse.dto.RenthouseDto;
import com.renthouse.dto.RoomDto;
import com.renthouse.entity.*;
import com.renthouse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private RenthouseRepository renthouseRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RenthouseDto> getNearbyRenthouses(Double latitude, Double longitude, Double radiusKm) {
        List<Renthouse> renthouses = renthouseRepository.findNearbyRenthouses(latitude, longitude, radiusKm);
        return renthouses.stream().map(this::convertToRenthouseDto).collect(Collectors.toList());
    }

    public List<RenthouseDto> getFeaturedRenthouses() {
        // Get the most recent 6 renthouses with available rooms
        Pageable pageable = PageRequest.of(0, 6);
        List<Renthouse> renthouses = renthouseRepository.findFeaturedRenthouses(pageable);
        return renthouses.stream().map(this::convertToRenthouseDto).collect(Collectors.toList());
    }

    public List<RenthouseDto> searchRenthouses(String name, String location, BigDecimal minPrice, BigDecimal maxPrice) {
        if (name == null && location == null && minPrice == null && maxPrice == null) {
            return renthouseRepository.findAllRenthouses().stream()
                    .map(this::convertToRenthouseDto)
                    .collect(Collectors.toList());
        }
        List<Renthouse> renthouses = renthouseRepository.searchRenthouses(name, location, minPrice, maxPrice);
        return renthouses.stream().map(this::convertToRenthouseDto).collect(Collectors.toList());
    }

    public RenthouseDto getRenthouseDetails(Long renthouseId) {
        System.out.println("Getting renthouse details for ID: " + renthouseId);
        Renthouse renthouse = renthouseRepository.findById(renthouseId)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));
        System.out.println("Found renthouse: " + renthouse.getName());
        System.out.println("Number of floors: " + renthouse.getFloors().size());
        RenthouseDto dto = convertToRenthouseDto(renthouse);
        System.out.println("Converted to DTO with " + dto.getFloors().size() + " floors");
        return dto;
    }

    public List<RoomDto> getAvailableRooms(Long renthouseId) {
        List<Room> rooms = roomRepository.findAvailableRoomsByRenthouse(renthouseId);
        User currentUser = getCurrentUser();
        return rooms.stream().map(room -> convertToRoomDto(room, currentUser.getId())).collect(Collectors.toList());
    }

    @Transactional
    public RoomDto bookRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getStatus() != Room.RoomStatus.AVAILABLE) {
            throw new RuntimeException("Room is not available for booking");
        }

        User currentUser = getCurrentUser();
        room.setRenter(currentUser);
        room.setStatus(Room.RoomStatus.BOOKED);
        room.setBookedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);
        return convertToRoomDto(savedRoom, currentUser.getId());
    }

    @Transactional
    public void addToFavorites(Long roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (favoriteRepository.existsByUser_IdAndRoom_Id(currentUser.getId(), roomId)) {
            throw new RuntimeException("Room is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(currentUser);
        favorite.setRoom(room);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFromFavorites(Long roomId) {
        User currentUser = getCurrentUser();
        favoriteRepository.deleteByUser_IdAndRoom_Id(currentUser.getId(), roomId);
    }

    @Transactional
    public void addRenthouseToFavorites(Long renthouseId) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(renthouseId)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        // Find the first available room in the renthouse
        Room availableRoom = null;
        for (Floor floor : renthouse.getFloors()) {
            for (Room room : floor.getRooms()) {
                if (room.getStatus() == Room.RoomStatus.AVAILABLE) {
                    availableRoom = room;
                    break;
                }
            }
            if (availableRoom != null) break;
        }

        if (availableRoom == null) {
            throw new RuntimeException("No available rooms in this renthouse");
        }

        // Check if already in favorites
        if (favoriteRepository.existsByUser_IdAndRoom_Id(currentUser.getId(), availableRoom.getId())) {
            throw new RuntimeException("Renthouse is already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(currentUser);
        favorite.setRoom(availableRoom);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeRenthouseFromFavorites(Long renthouseId) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(renthouseId)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        // Remove all rooms from this renthouse from favorites
        for (Floor floor : renthouse.getFloors()) {
            for (Room room : floor.getRooms()) {
                favoriteRepository.deleteByUser_IdAndRoom_Id(currentUser.getId(), room.getId());
            }
        }
    }

    public boolean isRenthouseInFavorites(Long renthouseId) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(renthouseId)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        // Check if any room from this renthouse is in favorites
        for (Floor floor : renthouse.getFloors()) {
            for (Room room : floor.getRooms()) {
                if (favoriteRepository.existsByUser_IdAndRoom_Id(currentUser.getId(), room.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<RoomDto> getCurrentBooking() {
        User currentUser = getCurrentUser();
        List<Room.RoomStatus> activeStatuses = List.of(Room.RoomStatus.BOOKED, Room.RoomStatus.OCCUPIED);
        
        return roomRepository.findFirstByRenterIdAndStatusIn(currentUser.getId(), activeStatuses)
                .map(room -> convertToRoomDto(room, currentUser.getId()));
    }

    public List<RoomDto> getAllMyBookings() {
        User currentUser = getCurrentUser();
        return roomRepository.findByRenterId(currentUser.getId()).stream()
                .map(room -> convertToRoomDto(room, currentUser.getId()))
                .collect(Collectors.toList());
    }

    public List<RoomDto> getFavoriteRooms() {
        User currentUser = getCurrentUser();
        List<Favorite> favorites = favoriteRepository.findByUser_Id(currentUser.getId());
        return favorites.stream()
                .map(favorite -> convertToRoomDto(favorite.getRoom(), currentUser.getId()))
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getMyPayments() {
        User currentUser = getCurrentUser();
        List<Payment> payments = paymentRepository.findByUser_Id(currentUser.getId());
        return payments.stream().map(this::convertToPaymentDto).collect(Collectors.toList());
    }

    public List<PaymentDto> getMyPendingPayments() {
        User currentUser = getCurrentUser();
        System.out.println("DEBUG: Getting pending payments for user ID: " + currentUser.getId());
        
        // Get only PENDING and OVERDUE payments, excluding CANCELLED and PAID
        List<Payment.PaymentStatus> pendingStatuses = List.of(Payment.PaymentStatus.PENDING, Payment.PaymentStatus.OVERDUE);
        List<Payment> pendingPayments = paymentRepository.findByUser_IdAndStatusInOrderByPaymentMonthAsc(currentUser.getId(), pendingStatuses);
        System.out.println("DEBUG: Found " + pendingPayments.size() + " payments with PENDING/OVERDUE status");
        
        for (Payment payment : pendingPayments) {
            System.out.println("DEBUG: Payment Entity - ID=" + payment.getId() + 
                             ", Status=" + payment.getStatus() + 
                             ", UserID=" + payment.getUser().getId() + 
                             ", RoomID=" + (payment.getRoom() != null ? payment.getRoom().getId() : "null"));
        }
        
        return pendingPayments.stream().map(this::convertToPaymentDto).collect(Collectors.toList());
    }

    public List<PaymentDto> getMyPaymentsByStatus(String status) {
        User currentUser = getCurrentUser();
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentRepository.findByUser_IdAndStatusOrderByPaymentMonthDesc(currentUser.getId(), paymentStatus);
            return payments.stream().map(this::convertToPaymentDto).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment status: " + status);
        }
    }

    public List<PaymentDto> getPaymentsForRoom(Long roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getRenter() == null || !room.getRenter().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied to this room's payments");
        }

        List<Payment> payments = paymentRepository.findByRoom_Id(roomId);
        return payments.stream().map(this::convertToPaymentDto).collect(Collectors.toList());
    }

    public String getPaymentQrCode(Long paymentId) {
        User currentUser = getCurrentUser();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return payment.getQrCodeData();
    }

    public RoomDto getRoomById(Long roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getRenter() == null || !room.getRenter().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied to this room's details");
        }

        return convertToRoomDto(room, currentUser.getId());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private RenthouseDto convertToRenthouseDto(Renthouse renthouse) {
        RenthouseDto dto = new RenthouseDto();
        dto.setId(renthouse.getId());
        dto.setName(renthouse.getName());
        dto.setAddress(renthouse.getAddress());
        dto.setDescription(renthouse.getDescription());
        dto.setLatitude(renthouse.getLatitude());
        dto.setLongitude(renthouse.getLongitude());
        dto.setBaseRent(renthouse.getBaseRent());
        dto.setWaterFee(renthouse.getWaterFee());
        dto.setElectricityFee(renthouse.getElectricityFee());
        dto.setImageUrl(renthouse.getImageUrl());
        dto.setQrCodeImage(renthouse.getQrCodeImage());
        dto.setCreatedAt(renthouse.getCreatedAt());
        dto.setUpdatedAt(renthouse.getUpdatedAt());
        dto.setOwnerId(renthouse.getOwner().getId());
        dto.setOwnerName(renthouse.getOwner().getFullName());
        
        // Set default amenities (empty list for now)
        dto.setAmenities(new ArrayList<>());
        
        // Convert floors and rooms
        User currentUser = getCurrentUser();
        List<FloorDto> floorDtos = renthouse.getFloors().stream()
                .map(floor -> convertToFloorDto(floor, currentUser.getId()))
                .collect(Collectors.toList());
        dto.setFloors(floorDtos);
        
        return dto;
    }
    
    private FloorDto convertToFloorDto(Floor floor, Long currentUserId) {
        FloorDto dto = new FloorDto();
        dto.setId(floor.getId());
        dto.setFloorNumber(floor.getFloorNumber());
        dto.setDescription(floor.getDescription());
        dto.setCreatedAt(floor.getCreatedAt());
        dto.setUpdatedAt(floor.getUpdatedAt());
        dto.setRenthouseId(floor.getRenthouse().getId());
        dto.setRenthouseName(floor.getRenthouse().getName());
        
        // Convert rooms
        List<RoomDto> roomDtos = floor.getRooms().stream()
                .map(room -> convertToRoomDto(room, currentUserId))
                .collect(Collectors.toList());
        dto.setRooms(roomDtos);
        
        return dto;
    }

    private RoomDto convertToRoomDto(Room room, Long currentUserId) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setDescription(room.getDescription());
        dto.setMonthlyRent(room.getMonthlyRent());
        dto.setDeposit(room.getDeposit());
        dto.setStatus(room.getStatus());
        dto.setBookedAt(room.getBookedAt());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        dto.setFloorId(room.getFloor().getId());
        dto.setFloorNumber(room.getFloor().getFloorNumber());
        dto.setRenthouseId(room.getFloor().getRenthouse().getId());
        dto.setRenthouseName(room.getFloor().getRenthouse().getName());
        
        if (room.getRenter() != null) {
            dto.setRenterId(room.getRenter().getId());
            dto.setRenterName(room.getRenter().getFullName());
            dto.setRenterFullName(room.getRenter().getFullName());
        }
        
        // Set isOccupied based on room status
        dto.setIsOccupied(room.getStatus() != Room.RoomStatus.AVAILABLE);
        
        dto.setIsFavorite(favoriteRepository.existsByUser_IdAndRoom_Id(currentUserId, room.getId()));
        return dto;
    }

    private PaymentDto convertToPaymentDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setType(payment.getType());
        dto.setPaymentMonth(payment.getPaymentMonth());
        dto.setRoomFee(payment.getRoomFee());
        dto.setElectricityFee(payment.getElectricityFee());
        dto.setWaterFee(payment.getWaterFee());
        dto.setOtherCharges(payment.getOtherCharges());
        dto.setOtherChargesDescription(payment.getOtherChargesDescription());
        dto.setTotalAmount(payment.getTotalAmount());
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : null);
        dto.setQrCodeData(payment.getQrCodeData());
        dto.setPaidAt(payment.getPaidAt());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        if (payment.getRoom() != null) {
            dto.setRoomId(payment.getRoom().getId());
            dto.setRoomNumber(payment.getRoom().getRoomNumber());
        }
        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getId());
            dto.setUserName(payment.getUser().getFullName());
        }
        return dto;
    }
}