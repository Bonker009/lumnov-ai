package com.renthouse.controller;

import com.renthouse.dto.*;
import com.renthouse.dto.request.*;
import com.renthouse.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasRole('OWNER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Owner", description = "Owner APIs for renthouse management")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/renthouses")
    @Operation(summary = "Get my renthouses", description = "Get all renthouses owned by the current user")
    public ResponseEntity<ApiResponse<List<RenthouseDto>>> getMyRenthouses() {
        try {
            List<RenthouseDto> renthouses = ownerService.getMyRenthouses();
            return ResponseEntity.ok(ApiResponse.success("Renthouses retrieved successfully", renthouses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get renthouses: " + e.getMessage()));
        }
    }

    @GetMapping("/renthouses/{id}")
    @Operation(summary = "Get renthouse by ID", description = "Get a specific renthouse owned by the current user")
    public ResponseEntity<ApiResponse<RenthouseDto>> getRenthouseById(@PathVariable Long id) {
        try {
            RenthouseDto renthouse = ownerService.getRenthouseById(id);
            return ResponseEntity.ok(ApiResponse.success("Renthouse retrieved successfully", renthouse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get renthouse: " + e.getMessage()));
        }
    }

    @PostMapping("/renthouses")
    @Operation(summary = "Create renthouse", description = "Create a new renthouse")
    public ResponseEntity<ApiResponse<RenthouseDto>> createRenthouse(@Valid @RequestBody CreateRenthouseRequest request) {
        try {
            RenthouseDto createdRenthouse = ownerService.createRenthouse(request);
            return ResponseEntity.ok(ApiResponse.success("Renthouse created successfully", createdRenthouse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create renthouse: " + e.getMessage()));
        }
    }

    @PutMapping("/renthouses/{id}")
    @Operation(summary = "Update renthouse", description = "Update an existing renthouse")
    public ResponseEntity<ApiResponse<RenthouseDto>> updateRenthouse(@PathVariable Long id, @Valid @RequestBody CreateRenthouseRequest request) {
        try {
            RenthouseDto updatedRenthouse = ownerService.updateRenthouse(id, request);
            return ResponseEntity.ok(ApiResponse.success("Renthouse updated successfully", updatedRenthouse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update renthouse: " + e.getMessage()));
        }
    }

    @DeleteMapping("/renthouses/{id}")
    @Operation(summary = "Delete renthouse", description = "Delete a renthouse")
    public ResponseEntity<ApiResponse<String>> deleteRenthouse(@PathVariable Long id) {
        try {
            ownerService.deleteRenthouse(id);
            return ResponseEntity.ok(ApiResponse.success("Renthouse deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete renthouse: " + e.getMessage()));
        }
    }

    @PostMapping("/renthouses/{renthouseId}/floors")
    @Operation(summary = "Create floor", description = "Create a new floor in a renthouse")
    public ResponseEntity<ApiResponse<FloorDto>> createFloor(@PathVariable Long renthouseId, @Valid @RequestBody CreateFloorRequest request) {
        try {
            FloorDto createdFloor = ownerService.createFloor(renthouseId, request);
            return ResponseEntity.ok(ApiResponse.success("Floor created successfully", createdFloor));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create floor: " + e.getMessage()));
        }
    }

    @PostMapping("/floors/{floorId}/rooms")
    @Operation(summary = "Create room", description = "Create a new room in a floor")
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(@PathVariable Long floorId, @Valid @RequestBody CreateRoomRequest request) {
        try {
            RoomDto createdRoom = ownerService.createRoom(floorId, request);
            return ResponseEntity.ok(ApiResponse.success("Room created successfully", createdRoom));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create room: " + e.getMessage()));
        }
    }

    @PutMapping("/rooms/{roomId}")
    @Operation(summary = "Update room", description = "Update an existing room")
    public ResponseEntity<ApiResponse<RoomDto>> updateRoom(@PathVariable Long roomId, @Valid @RequestBody CreateRoomRequest request) {
        try {
            RoomDto updatedRoom = ownerService.updateRoom(roomId, request);
            return ResponseEntity.ok(ApiResponse.success("Room updated successfully", updatedRoom));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update room: " + e.getMessage()));
        }
    }

    @GetMapping("/rooms")
    @Operation(summary = "Get my rooms", description = "Get all rooms owned by the current user")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getMyRooms() {
        try {
            List<RoomDto> rooms = ownerService.getMyRooms();
            return ResponseEntity.ok(ApiResponse.success("Rooms retrieved successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get rooms: " + e.getMessage()));
        }
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Get room by ID", description = "Get a specific room with full details")
    public ResponseEntity<ApiResponse<RoomDto>> getRoomById(@PathVariable Long roomId) {
        try {
            RoomDto room = ownerService.getRoomById(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room retrieved successfully", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get room: " + e.getMessage()));
        }
    }

    @GetMapping("/rooms/search")
    @Operation(summary = "Search my rooms", description = "Search rooms by room number or renter username")
    public ResponseEntity<ApiResponse<List<RoomDto>>> searchMyRooms(
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String username) {
        try {
            List<RoomDto> rooms = ownerService.searchMyRooms(roomNumber, username);
            return ResponseEntity.ok(ApiResponse.success("Room search completed successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to search rooms: " + e.getMessage()));
        }
    }

    @PostMapping("/payments")
    @Operation(summary = "Create payment", description = "Create a new payment record")
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        try {
            PaymentDto createdPayment = ownerService.createPayment(request);
            return ResponseEntity.ok(ApiResponse.success("Payment created successfully", createdPayment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create payment: " + e.getMessage()));
        }
    }

    @GetMapping("/payments")
    @Operation(summary = "Get my payments", description = "Get all payment records for properties owned by the current user")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyPayments() {
        try {
            List<PaymentDto> payments = ownerService.getMyPayments();
            return ResponseEntity.ok(ApiResponse.success("Payment records retrieved successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get payment records: " + e.getMessage()));
        }
    }

    @GetMapping("/rooms/{roomId}/payments")
    @Operation(summary = "Get room payments", description = "Get all payment records for a specific room")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getRoomPayments(@PathVariable Long roomId) {
        try {
            List<PaymentDto> payments = ownerService.getRoomPayments(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room payment records retrieved successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get room payment records: " + e.getMessage()));
        }
    }

    @GetMapping("/income/monthly")
    @Operation(summary = "Get monthly income", description = "Get income summary for a specific month and year")
    public ResponseEntity<ApiResponse<IncomeReportDto>> getMonthlyIncome(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            IncomeReportDto incomeReport = ownerService.getMonthlyIncome(year, month);
            return ResponseEntity.ok(ApiResponse.success("Monthly income retrieved successfully", incomeReport));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get monthly income: " + e.getMessage()));
        }
    }

    @GetMapping("/income/yearly")
    @Operation(summary = "Get yearly income", description = "Get income summary for a specific year")
    public ResponseEntity<ApiResponse<IncomeReportDto>> getYearlyIncome(@RequestParam int year) {
        try {
            IncomeReportDto incomeReport = ownerService.getYearlyIncome(year);
            return ResponseEntity.ok(ApiResponse.success("Yearly income report retrieved successfully", incomeReport));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get yearly income: " + e.getMessage()));
        }
    }

    @GetMapping("/tenants")
    @Operation(summary = "Get all tenants", description = "Get all tenants for properties owned by the current user")
    public ResponseEntity<ApiResponse<List<TenantDto>>> getAllTenants() {
        try {
            List<TenantDto> tenants = ownerService.getAllTenants();
            return ResponseEntity.ok(ApiResponse.success("Tenants retrieved successfully", tenants));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get tenants: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/active-rooms")
    @Operation(summary = "Get active rooms count", description = "Get the count of occupied rooms for the current owner")
    public ResponseEntity<ApiResponse<Long>> getActiveRoomsCount() {
        try {
            long count = ownerService.getActiveRoomsCount();
            return ResponseEntity.ok(ApiResponse.success("Active rooms count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get active rooms count: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/pending-payments")
    @Operation(summary = "Get pending payments count", description = "Get the count of pending payments for the current owner")
    public ResponseEntity<ApiResponse<Long>> getPendingPaymentsCount() {
        try {
            long count = ownerService.getPendingPaymentsCount();
            return ResponseEntity.ok(ApiResponse.success("Pending payments count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get pending payments count: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get dashboard analytics", description = "Get comprehensive analytics data for the dashboard including monthly income and tenant statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardAnalytics() {
        try {
            Map<String, Object> analytics = ownerService.getDashboardAnalytics();
            return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get analytics: " + e.getMessage()));
        }
    }

    @PutMapping("/payments/{paymentId}/status")
    @Operation(summary = "Update payment status", description = "Update payment status to PAID")
    public ResponseEntity<ApiResponse<PaymentDto>> updatePaymentStatus(@PathVariable Long paymentId) {
        try {
            PaymentDto updatedPayment = ownerService.updatePaymentStatus(paymentId);
            return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", updatedPayment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update payment status: " + e.getMessage()));
        }
    }
}