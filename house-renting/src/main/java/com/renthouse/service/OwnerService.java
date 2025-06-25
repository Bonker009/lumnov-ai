package com.renthouse.service;

import com.renthouse.dto.*;
import com.renthouse.dto.request.*;
import com.renthouse.entity.*;
import com.renthouse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    @Autowired
    private RenthouseRepository renthouseRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<RenthouseDto> getMyRenthouses() {
        User currentUser = getCurrentUser();
        List<Renthouse> renthouses = renthouseRepository.findByOwner_Id(currentUser.getId());
        return renthouses.stream().map(this::convertToRenthouseDto).collect(Collectors.toList());
    }

    public RenthouseDto getRenthouseById(Long id) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        if (!renthouse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToRenthouseDto(renthouse);
    }

    @Transactional
    public RenthouseDto createRenthouse(CreateRenthouseRequest request) {
        User currentUser = getCurrentUser();
        
        Renthouse renthouse = new Renthouse();
        renthouse.setName(request.getName());
        renthouse.setAddress(request.getAddress());
        renthouse.setDescription(request.getDescription());
        renthouse.setLatitude(request.getLatitude());
        renthouse.setLongitude(request.getLongitude());
        renthouse.setBaseRent(request.getBaseRent());
        renthouse.setWaterFee(new BigDecimal(request.getWaterFee()));
        renthouse.setElectricityFee(new BigDecimal(request.getElectricityFee()));
        renthouse.setImageUrl(request.getImageUrl());
        renthouse.setQrCodeImage(request.getQrCodeImage());
        renthouse.setOwner(currentUser);

        Renthouse savedRenthouse = renthouseRepository.save(renthouse);
        return convertToRenthouseDto(savedRenthouse);
    }

    @Transactional
    public RenthouseDto updateRenthouse(Long id, CreateRenthouseRequest request) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        if (!renthouse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        renthouse.setName(request.getName());
        renthouse.setAddress(request.getAddress());
        renthouse.setDescription(request.getDescription());
        renthouse.setLatitude(request.getLatitude());
        renthouse.setLongitude(request.getLongitude());
        renthouse.setBaseRent(request.getBaseRent());
        renthouse.setWaterFee(new BigDecimal(request.getWaterFee()));
        renthouse.setElectricityFee(new BigDecimal(request.getElectricityFee()));
        renthouse.setImageUrl(request.getImageUrl());
        renthouse.setQrCodeImage(request.getQrCodeImage());

        Renthouse savedRenthouse = renthouseRepository.save(renthouse);
        return convertToRenthouseDto(savedRenthouse);
    }

    @Transactional
    public void deleteRenthouse(Long id) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        if (!renthouse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        renthouseRepository.delete(renthouse);
    }

    @Transactional
    public FloorDto createFloor(Long renthouseId, CreateFloorRequest request) {
        User currentUser = getCurrentUser();
        Renthouse renthouse = renthouseRepository.findById(renthouseId)
                .orElseThrow(() -> new RuntimeException("Renthouse not found"));

        if (!renthouse.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Auto-generate floor number if not provided
        Integer floorNumber = request.getFloorNumber();
        if (floorNumber == null) {
            Integer maxFloorNumber = floorRepository.findMaxFloorNumberByRenthouseId(renthouseId);
            floorNumber = (maxFloorNumber != null) ? maxFloorNumber + 1 : 1;
        }

        Floor floor = new Floor();
        floor.setFloorNumber(floorNumber);
        floor.setDescription(request.getDescription());
        floor.setRenthouse(renthouse);

        Floor savedFloor = floorRepository.save(floor);
        return convertToFloorDto(savedFloor);
    }

    @Transactional
    public RoomDto createRoom(Long floorId, CreateRoomRequest request) {
        User currentUser = getCurrentUser();
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("Floor not found"));

        if (!floor.getRenthouse().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Auto-generate room number if not provided
        String roomNumber = request.getRoomNumber();
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            Integer maxRoomNumber = roomRepository.findMaxRoomNumberByFloorId(floorId);
            int nextRoomNumber = (maxRoomNumber != null) ? maxRoomNumber + 1 : 1;
            roomNumber = String.valueOf(nextRoomNumber);
        }

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setDescription(request.getDescription());
        room.setMonthlyRent(request.getMonthlyRent());
        room.setDeposit(request.getDeposit());
        room.setStatus(Room.RoomStatus.AVAILABLE);
        room.setFloor(floor);

        Room savedRoom = roomRepository.save(room);
        return convertToRoomDto(savedRoom);
    }

    @Transactional
    public RoomDto updateRoom(Long roomId, CreateRoomRequest request) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getFloor().getRenthouse().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setDescription(request.getDescription());
        room.setMonthlyRent(request.getMonthlyRent());
        room.setDeposit(request.getDeposit());

        Room savedRoom = roomRepository.save(room);
        return convertToRoomDto(savedRoom);
    }

    public List<RoomDto> getMyRooms() {
        User currentUser = getCurrentUser();
        List<Room> rooms = roomRepository.findByOwnerId(currentUser.getId());
        return rooms.stream().map(this::convertToRoomDto).collect(Collectors.toList());
    }

    public RoomDto getRoomById(Long roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        if (!room.getFloor().getRenthouse().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToRoomDtoWithDetails(room);
    }

    public List<RoomDto> searchMyRooms(String roomNumber, String username) {
        User currentUser = getCurrentUser();
        List<Room> rooms = roomRepository.searchRoomsByOwner(roomNumber, username, currentUser.getId());
        return rooms.stream().map(this::convertToRoomDto).collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto createPayment(CreatePaymentRequest request) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getFloor().getRenthouse().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (room.getRenter() == null) {
            throw new RuntimeException("Room has no renter");
        }

        Payment payment = new Payment();
        payment.setPaymentMonth(request.getPaymentMonth());
        payment.setRoomFee(request.getRoomFee());
        payment.setElectricityFee(request.getElectricityFee());
        payment.setWaterFee(request.getWaterFee());
        payment.setOtherCharges(request.getOtherCharges());
        payment.setOtherChargesDescription(request.getOtherChargesDescription());
        payment.setQrCodeData(request.getQrCodeData());
        payment.setRoom(room);
        payment.setUser(room.getRenter());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToPaymentDto(savedPayment);
    }

    public List<PaymentDto> getMyPayments() {
        User currentUser = getCurrentUser();
        List<Payment> payments = paymentRepository.findByOwnerId(currentUser.getId());
        return payments.stream().map(this::convertToPaymentDto).collect(Collectors.toList());
    }

    public List<PaymentDto> getRoomPayments(Long roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getFloor().getRenthouse().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        List<Payment> payments = paymentRepository.findByRoom_IdOrderByPaymentMonthDesc(roomId);
        return payments.stream().map(this::convertToPaymentDto).collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto updatePaymentStatus(Long paymentId) {
        User currentUser = getCurrentUser();
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Check if the payment belongs to a room owned by the current user
        if (!payment.getRoom().getFloor().getRenthouse().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Update payment status to PAID
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        return convertToPaymentDto(savedPayment);
    }

    public IncomeReportDto getMonthlyIncome(int year, int month) {
        User currentUser = getCurrentUser();
        BigDecimal income = paymentRepository.getMonthlyIncomeByOwner(currentUser.getId(), year, month);
        if (income == null) income = BigDecimal.ZERO;
        
        LocalDate period = LocalDate.of(year, month, 1);
        return new IncomeReportDto(period, income, "MONTHLY");
    }

    public IncomeReportDto getYearlyIncome(int year) {
        User currentUser = getCurrentUser();
        BigDecimal income = paymentRepository.getYearlyIncomeByOwner(currentUser.getId(), year);
        if (income == null) income = BigDecimal.ZERO;
        
        LocalDate period = LocalDate.of(year, 1, 1);
        return new IncomeReportDto(period, income, "YEARLY");
    }

    public List<TenantDto> getAllTenants() {
        User currentUser = getCurrentUser();
        List<Room> rooms = roomRepository.findByOwnerId(currentUser.getId());
        
        System.out.println("Found " + rooms.size() + " rooms for owner " + currentUser.getId());
        
        List<TenantDto> result = rooms.stream()
            .filter(room -> room.getRenter() != null || room.getBookedAt() != null) // Include rooms that have been booked/rented
            .peek(room -> System.out.println("Processing room: " + room.getId() + " - " + room.getRoomNumber() + 
                  " (renter: " + (room.getRenter() != null ? room.getRenter().getUsername() : "null") + 
                  ", bookedAt: " + room.getBookedAt() + ")"))
            .map(this::convertToTenantDto)
            .filter(tenant -> tenant != null) // Filter out any null results
            .collect(Collectors.toList());
            
        System.out.println("Returning " + result.size() + " tenant records");
        return result;
    }

    public long getActiveRoomsCount() {
        User currentUser = getCurrentUser();
        return roomRepository.countByOwnerIdAndStatusNot(currentUser.getId(), Room.RoomStatus.AVAILABLE);
    }

    public long getPendingPaymentsCount() {
        User currentUser = getCurrentUser();
        return paymentRepository.countByOwnerIdAndStatusNot(currentUser.getId(), Payment.PaymentStatus.PAID);
    }

    public Map<String, Object> getDashboardAnalytics() {
        User currentUser = getCurrentUser();
        int currentYear = LocalDate.now().getYear();
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Get monthly income data for bar chart
        List<Map<String, Object>> monthlyIncome = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal income = paymentRepository.getMonthlyIncomeByOwner(currentUser.getId(), currentYear, month);
            if (income == null) income = BigDecimal.ZERO;
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month);
            monthData.put("name", LocalDate.of(currentYear, month, 1).getMonth().name().substring(0, 3));
            monthData.put("income", income.doubleValue());
            monthlyIncome.add(monthData);
        }
        
        // Get tenant statistics for donut chart
        List<TenantDto> tenants = getAllTenants();
        Map<String, Object> tenantStats = new HashMap<>();
        tenantStats.put("totalTenants", tenants.size());
        
        // Since we don't have gender data, we'll create some meaningful statistics
        // You can replace this with actual gender-based grouping when gender field is added
        List<Map<String, Object>> tenantDistribution = new ArrayList<>();
        
        // Group by payment status for now (as a placeholder for gender)
        Map<String, Long> statusCounts = tenants.stream()
            .collect(Collectors.groupingBy(
                tenant -> tenant.getPaymentStatus() != null ? tenant.getPaymentStatus() : "UNKNOWN",
                Collectors.counting()
            ));
        
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("name", entry.getKey());
            statusData.put("value", entry.getValue());
            tenantDistribution.add(statusData);
        }
        
        // If no data, add a placeholder
        if (tenantDistribution.isEmpty()) {
            Map<String, Object> placeholder = new HashMap<>();
            placeholder.put("name", "No Data");
            placeholder.put("value", 0);
            tenantDistribution.add(placeholder);
        }
        
        tenantStats.put("distribution", tenantDistribution);
        
        // Get summary statistics
        long totalProperties = renthouseRepository.countByOwner_Id(currentUser.getId());
        long activeRooms = getActiveRoomsCount();
        long pendingPayments = getPendingPaymentsCount();
        BigDecimal totalYearIncome = monthlyIncome.stream()
            .mapToDouble(m -> (Double) m.get("income"))
            .mapToObj(BigDecimal::valueOf)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        analytics.put("monthlyIncome", monthlyIncome);
        analytics.put("tenantStats", tenantStats);
        analytics.put("summary", Map.of(
            "totalProperties", totalProperties,
            "activeRooms", activeRooms,
            "pendingPayments", pendingPayments,
            "totalYearIncome", totalYearIncome.doubleValue()
        ));
        
        return analytics;
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

    private FloorDto convertToFloorDto(Floor floor) {
        FloorDto dto = new FloorDto();
        dto.setId(floor.getId());
        dto.setFloorNumber(floor.getFloorNumber());
        dto.setDescription(floor.getDescription());
        dto.setCreatedAt(floor.getCreatedAt());
        dto.setUpdatedAt(floor.getUpdatedAt());
        dto.setRenthouseId(floor.getRenthouse().getId());
        dto.setRenthouseName(floor.getRenthouse().getName());
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

    private RoomDto convertToRoomDto(Room room) {
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
            dto.setRenterUsername(room.getRenter().getUsername());
            dto.setRenterEmail(room.getRenter().getEmail());
            dto.setRenterPhone(room.getRenter().getPhoneNumber());
            dto.setMoveInDate(room.getBookedAt());
        }
        
        // Set isOccupied based on room status
        dto.setIsOccupied(room.getStatus() != Room.RoomStatus.AVAILABLE);
        
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
            dto.setRenterUsername(room.getRenter().getUsername());
            dto.setRenterEmail(room.getRenter().getEmail());
            dto.setRenterPhone(room.getRenter().getPhoneNumber());
            dto.setMoveInDate(room.getBookedAt());
        }
        
        // Set isOccupied based on room status
        dto.setIsOccupied(room.getStatus() != Room.RoomStatus.AVAILABLE);
        
        return dto;
    }

    private RoomDto convertToRoomDtoWithDetails(Room room) {
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
        dto.setRenthouseAddress(room.getFloor().getRenthouse().getAddress());
        
        // Set detailed renter information if available
        if (room.getRenter() != null) {
            dto.setRenterId(room.getRenter().getId());
            dto.setRenterName(room.getRenter().getFullName());
            dto.setRenterFullName(room.getRenter().getFullName());
            dto.setRenterUsername(room.getRenter().getUsername());
            dto.setRenterEmail(room.getRenter().getEmail());
            dto.setRenterPhone(room.getRenter().getPhoneNumber());
            dto.setMoveInDate(room.getBookedAt());
            // TODO: Add lease end date if available in the Room entity
        }
        
        // Set isOccupied based on room status
        dto.setIsOccupied(room.getStatus() != Room.RoomStatus.AVAILABLE);
        
        return dto;
    }

    private PaymentDto convertToPaymentDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setRoomId(payment.getRoom().getId());
        dto.setRoomNumber(payment.getRoom().getRoomNumber());
        dto.setUserId(payment.getUser().getId());
        dto.setUserName(payment.getUser().getFullName());
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
        return dto;
    }

    private TenantDto convertToTenantDto(Room room) {
        User renter = room.getRenter();
        
        // If no current renter but room has been booked, we need to handle this case
        if (renter == null && room.getBookedAt() != null) {
            // This room was booked but renter is no longer active
            // We'll create a tenant record with basic room info
            return new TenantDto(
                room.getId(), // Use room ID as tenant ID for historical records
                "former_tenant_" + room.getId(),
                "Former Tenant",
                "N/A",
                "N/A",
                room.getId(),
                room.getRoomNumber(),
                room.getFloor().getFloorNumber(),
                room.getFloor().getRenthouse().getId(),
                room.getFloor().getRenthouse().getName(),
                room.getFloor().getRenthouse().getAddress(),
                room.getMonthlyRent(),
                room.getDeposit(),
                room.getBookedAt() != null ? room.getBookedAt().toLocalDate() : LocalDate.now(),
                null,
                null,
                "UNPAID",
                BigDecimal.ZERO,
                room.getMonthlyRent(),
                false // Not active
            );
        }
        
        if (renter == null) {
            return null;
        }

        // Calculate payment status and outstanding balance
        String paymentStatus = "UNPAID";
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal outstandingBalance = BigDecimal.ZERO;
        LocalDateTime lastPaymentDate = null;
        LocalDateTime nextPaymentDate = null;

        // Get the latest payment for this room
        List<Payment> payments = paymentRepository.findByRoom_IdOrderByPaymentMonthDesc(room.getId());
        if (!payments.isEmpty()) {
            Payment latestPayment = payments.get(0);
            lastPaymentDate = latestPayment.getCreatedAt();
            
            // Calculate total paid
            totalPaid = payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.PAID)
                .map(Payment::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate outstanding balance
            outstandingBalance = room.getMonthlyRent().subtract(totalPaid);
            
            // Determine payment status
            if (latestPayment.getStatus() == Payment.PaymentStatus.PAID) {
                paymentStatus = "PAID";
            } else if (latestPayment.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30))) {
                paymentStatus = "OVERDUE";
            } else {
                paymentStatus = "PENDING";
            }
        }

        // Calculate next payment date (assuming monthly payments)
        if (lastPaymentDate != null) {
            nextPaymentDate = lastPaymentDate.plusMonths(1);
        }

        return new TenantDto(
            renter.getId(),
            renter.getUsername(),
            renter.getFullName(),
            renter.getEmail(),
            renter.getPhoneNumber(),
            room.getId(),
            room.getRoomNumber(),
            room.getFloor().getFloorNumber(),
            room.getFloor().getRenthouse().getId(),
            room.getFloor().getRenthouse().getName(),
            room.getFloor().getRenthouse().getAddress(),
            room.getMonthlyRent(),
            room.getDeposit(),
            room.getBookedAt() != null ? room.getBookedAt().toLocalDate() : LocalDate.now(),
            lastPaymentDate,
            nextPaymentDate,
            paymentStatus,
            totalPaid,
            outstandingBalance,
            room.getStatus() == Room.RoomStatus.OCCUPIED
        );
    }
}