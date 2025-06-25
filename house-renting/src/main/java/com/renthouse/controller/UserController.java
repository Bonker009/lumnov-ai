package com.renthouse.controller;

import com.renthouse.dto.ApiResponse;
import com.renthouse.dto.PaymentDto;
import com.renthouse.dto.RenthouseDto;
import com.renthouse.dto.RoomDto;
import com.renthouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "User APIs for renthouse management")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/renthouses/featured")
    @Operation(summary = "Get featured renthouses", description = "Get featured/popular renthouses for the dashboard")
    public ResponseEntity<ApiResponse<List<RenthouseDto>>> getFeaturedRenthouses() {
        try {
            List<RenthouseDto> renthouses = userService.getFeaturedRenthouses();
            return ResponseEntity.ok(ApiResponse.success("Featured renthouses retrieved successfully", renthouses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get featured renthouses: " + e.getMessage()));
        }
    }

    @GetMapping("/renthouses/nearby")
    @Operation(summary = "Get nearby renthouses", description = "Get popular renthouses near provided coordinates")
    public ResponseEntity<ApiResponse<List<RenthouseDto>>> getNearbyRenthouses(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        try {
            List<RenthouseDto> renthouses = userService.getNearbyRenthouses(latitude, longitude, radiusKm);
            return ResponseEntity.ok(ApiResponse.success("Nearby renthouses retrieved successfully", renthouses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get nearby renthouses: " + e.getMessage()));
        }
    }

    @GetMapping("/renthouses/search")
    @Operation(summary = "Search renthouses", description = "Search renthouses by name, location, or price range")
    public ResponseEntity<ApiResponse<List<RenthouseDto>>> searchRenthouses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        try {
            List<RenthouseDto> renthouses = userService.searchRenthouses(name, location, minPrice, maxPrice);
            return ResponseEntity.ok(ApiResponse.success("Renthouses search completed successfully", renthouses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to search renthouses: " + e.getMessage()));
        }
    }

    @GetMapping("/renthouses/{id}")
    @Operation(summary = "Get renthouse details", description = "Get detailed information about a renthouse")
    public ResponseEntity<ApiResponse<RenthouseDto>> getRenthouseDetails(@PathVariable Long id) {
        try {
            RenthouseDto renthouse = userService.getRenthouseDetails(id);
            return ResponseEntity.ok(ApiResponse.success("Renthouse details retrieved successfully", renthouse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get renthouse details: " + e.getMessage()));
        }
    }

    @GetMapping("/rooms/{id}")
    @Operation(summary = "Get room details by ID", description = "Get detailed information about a specific room")
    public ResponseEntity<ApiResponse<RoomDto>> getRoomById(@PathVariable Long id) {
        try {
            RoomDto room = userService.getRoomById(id);
            return ResponseEntity.ok(ApiResponse.success("Room details retrieved successfully", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get room details: " + e.getMessage()));
        }
    }

    @GetMapping("/rooms/{id}/payments")
    @Operation(summary = "Get payments for a room", description = "Get the payment history for a specific room")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentsForRoom(@PathVariable Long id) {
        try {
            List<PaymentDto> payments = userService.getPaymentsForRoom(id);
            return ResponseEntity.ok(ApiResponse.success("Room payments retrieved successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get room payments: " + e.getMessage()));
        }
    }

    @GetMapping("/renthouses/{id}/rooms/available")
    @Operation(summary = "Get available rooms", description = "Get all available rooms in a renthouse")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getAvailableRooms(@PathVariable Long id) {
        try {
            List<RoomDto> rooms = userService.getAvailableRooms(id);
            return ResponseEntity.ok(ApiResponse.success("Available rooms retrieved successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get available rooms: " + e.getMessage()));
        }
    }

    @PostMapping("/rooms/{id}/book")
    @Operation(summary = "Book a room", description = "Book an available room")
    public ResponseEntity<ApiResponse<RoomDto>> bookRoom(@PathVariable Long id) {
        try {
            RoomDto room = userService.bookRoom(id);
            return ResponseEntity.ok(ApiResponse.success("Room booked successfully", room));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to book room: " + e.getMessage()));
        }
    }

    @PostMapping("/favorites/{roomId}")
    @Operation(summary = "Add to favorites", description = "Add a room to favorites")
    public ResponseEntity<ApiResponse<String>> addToFavorites(@PathVariable Long roomId) {
        try {
            userService.addToFavorites(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room added to favorites successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add to favorites: " + e.getMessage()));
        }
    }

    @DeleteMapping("/favorites/{roomId}")
    @Operation(summary = "Remove from favorites", description = "Remove a room from favorites")
    public ResponseEntity<ApiResponse<String>> removeFromFavorites(@PathVariable Long roomId) {
        try {
            userService.removeFromFavorites(roomId);
            return ResponseEntity.ok(ApiResponse.success("Room removed from favorites successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to remove from favorites: " + e.getMessage()));
        }
    }

    @PostMapping("/renthouses/{renthouseId}/favorites")
    @Operation(summary = "Add renthouse to favorites", description = "Add a renthouse to favorites (adds first available room)")
    public ResponseEntity<ApiResponse<String>> addRenthouseToFavorites(@PathVariable Long renthouseId) {
        try {
            userService.addRenthouseToFavorites(renthouseId);
            return ResponseEntity.ok(ApiResponse.success("Renthouse added to favorites successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to add renthouse to favorites: " + e.getMessage()));
        }
    }

    @DeleteMapping("/renthouses/{renthouseId}/favorites")
    @Operation(summary = "Remove renthouse from favorites", description = "Remove a renthouse from favorites")
    public ResponseEntity<ApiResponse<String>> removeRenthouseFromFavorites(@PathVariable Long renthouseId) {
        try {
            userService.removeRenthouseFromFavorites(renthouseId);
            return ResponseEntity.ok(ApiResponse.success("Renthouse removed from favorites successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to remove renthouse from favorites: " + e.getMessage()));
        }
    }

    @GetMapping("/renthouses/{renthouseId}/favorites/check")
    @Operation(summary = "Check if renthouse is in favorites", description = "Check if a renthouse is in user's favorites")
    public ResponseEntity<ApiResponse<Boolean>> checkRenthouseInFavorites(@PathVariable Long renthouseId) {
        try {
            boolean isFavorite = userService.isRenthouseInFavorites(renthouseId);
            return ResponseEntity.ok(ApiResponse.success("Favorite status checked successfully", isFavorite));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to check favorite status: " + e.getMessage()));
        }
    }

    @GetMapping("/booking/current")
    @Operation(summary = "Get current booking", description = "Get the current user's active room booking")
    public ResponseEntity<ApiResponse<RoomDto>> getCurrentBooking() {
        try {
            Optional<RoomDto> booking = userService.getCurrentBooking();
            return booking.map(roomDto -> ResponseEntity.ok(ApiResponse.success("Current booking retrieved successfully", roomDto)))
                    .orElseGet(() -> ResponseEntity.ok(ApiResponse.success("No active booking found", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve current booking: " + e.getMessage()));
        }
    }

    @GetMapping("/bookings/all")
    @Operation(summary = "Get all my bookings", description = "Get a list of all rooms booked by the current user")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getAllMyBookings() {
        try {
            List<RoomDto> bookings = userService.getAllMyBookings();
            return ResponseEntity.ok(ApiResponse.success("All bookings retrieved successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve bookings: " + e.getMessage()));
        }
    }

    @GetMapping("/favorites")
    @Operation(summary = "Get favorite rooms", description = "Get all favorite rooms")
    public ResponseEntity<ApiResponse<List<RoomDto>>> getFavoriteRooms() {
        try {
            List<RoomDto> rooms = userService.getFavoriteRooms();
            return ResponseEntity.ok(ApiResponse.success("Favorite rooms retrieved successfully", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get favorite rooms: " + e.getMessage()));
        }
    }

    @GetMapping("/payments")
    @Operation(summary = "Get my payments", description = "Get all payment records for the current user")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyPayments() {
        try {
            List<PaymentDto> payments = userService.getMyPayments();
            return ResponseEntity.ok(ApiResponse.success("Payment records retrieved successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get payment records: " + e.getMessage()));
        }
    }

    @GetMapping("/payments/pending")
    @Operation(summary = "Get pending payments", description = "Get all pending/unpaid payment records for the current user")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyPendingPayments() {
        try {
            List<PaymentDto> pendingPayments = userService.getMyPendingPayments();
            System.out.println("DEBUG: Found " + pendingPayments.size() + " pending payments for user");
            for (PaymentDto payment : pendingPayments) {
                System.out.println("DEBUG: Payment ID=" + payment.getId() + ", Status=" + payment.getStatus() + 
                                 ", Month=" + payment.getPaymentMonth() + ", Amount=" + payment.getTotalAmount());
            }
            return ResponseEntity.ok(ApiResponse.success("Pending payment records retrieved successfully", pendingPayments));
        } catch (Exception e) {
            System.out.println("DEBUG: Error in getMyPendingPayments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get pending payment records: " + e.getMessage()));
        }
    }

    @GetMapping("/payments/status/{status}")
    @Operation(summary = "Get payments by status", description = "Get payment records filtered by status")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyPaymentsByStatus(@PathVariable String status) {
        try {
            List<PaymentDto> payments = userService.getMyPaymentsByStatus(status.toUpperCase());
            return ResponseEntity.ok(ApiResponse.success("Payment records retrieved successfully", payments));


        }        catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get payment records: " + e.getMessage()));
        }
    }

    @GetMapping("/payments/{id}/qr-code")
    @Operation(summary = "Get payment QR code", description = "Get QR code data for a payment")
    public ResponseEntity<ApiResponse<String>> getPaymentQrCode(@PathVariable Long id) {
        try {
            String qrCodeData = userService.getPaymentQrCode(id);
            return ResponseEntity.ok(ApiResponse.success("QR code retrieved successfully", qrCodeData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get QR code: " + e.getMessage()));
        }
    }
}