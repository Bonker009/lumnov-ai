package com.renthouse.dto;

import com.renthouse.entity.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoomDto {
    private Long id;

    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Room number must not exceed 20 characters")
    private String roomNumber;

    private String description;

    @PositiveOrZero(message = "Monthly rent must be positive or zero")
    private BigDecimal monthlyRent;

    @PositiveOrZero(message = "Deposit must be positive or zero")
    private BigDecimal deposit;

    private Room.RoomStatus status;
    private LocalDateTime bookedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long floorId;
    private Integer floorNumber;
    private Long renthouseId;
    private String renthouseName;
    private Long renterId;
    private String renterName;
    private Boolean isFavorite;
    
    // Add fields to match frontend expectations
    private Boolean isOccupied;
    private String renterFullName;
    private String renthouseAddress;
    private String renterUsername;
    private String renterEmail;
    private String renterPhone;
    private LocalDateTime moveInDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public Room.RoomStatus getStatus() {
        return status;
    }

    public void setStatus(Room.RoomStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public Long getRenthouseId() {
        return renthouseId;
    }

    public void setRenthouseId(Long renthouseId) {
        this.renthouseId = renthouseId;
    }

    public String getRenthouseName() {
        return renthouseName;
    }

    public void setRenthouseName(String renthouseName) {
        this.renthouseName = renthouseName;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
    
    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getRenterFullName() {
        return renterFullName;
    }

    public void setRenterFullName(String renterFullName) {
        this.renterFullName = renterFullName;
    }

    public String getRenthouseAddress() {
        return renthouseAddress;
    }

    public void setRenthouseAddress(String renthouseAddress) {
        this.renthouseAddress = renthouseAddress;
    }

    public String getRenterUsername() {
        return renterUsername;
    }

    public void setRenterUsername(String renterUsername) {
        this.renterUsername = renterUsername;
    }

    public String getRenterEmail() {
        return renterEmail;
    }

    public void setRenterEmail(String renterEmail) {
        this.renterEmail = renterEmail;
    }

    public String getRenterPhone() {
        return renterPhone;
    }

    public void setRenterPhone(String renterPhone) {
        this.renterPhone = renterPhone;
    }

    public LocalDateTime getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDateTime moveInDate) {
        this.moveInDate = moveInDate;
    }
}