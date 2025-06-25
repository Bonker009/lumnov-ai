package com.renthouse.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TenantDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Long roomId;
    private String roomNumber;
    private Integer floorNumber;
    private Long renthouseId;
    private String renthouseName;
    private String renthouseAddress;
    private BigDecimal monthlyRent;
    private BigDecimal deposit;
    private LocalDate moveInDate;
    private LocalDateTime lastPaymentDate;
    private LocalDateTime nextPaymentDate;
    private String paymentStatus;
    private BigDecimal totalPaid;
    private BigDecimal outstandingBalance;
    private boolean isActive;

    public TenantDto() {
    }

    public TenantDto(Long id, String username, String fullName, String email, String phone,
                    Long roomId, String roomNumber, Integer floorNumber, Long renthouseId,
                    String renthouseName, String renthouseAddress, BigDecimal monthlyRent,
                    BigDecimal deposit, LocalDate moveInDate, LocalDateTime lastPaymentDate,
                    LocalDateTime nextPaymentDate, String paymentStatus, BigDecimal totalPaid,
                    BigDecimal outstandingBalance, boolean isActive) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;
        this.renthouseId = renthouseId;
        this.renthouseName = renthouseName;
        this.renthouseAddress = renthouseAddress;
        this.monthlyRent = monthlyRent;
        this.deposit = deposit;
        this.moveInDate = moveInDate;
        this.lastPaymentDate = lastPaymentDate;
        this.nextPaymentDate = nextPaymentDate;
        this.paymentStatus = paymentStatus;
        this.totalPaid = totalPaid;
        this.outstandingBalance = outstandingBalance;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public String getRenthouseAddress() {
        return renthouseAddress;
    }

    public void setRenthouseAddress(String renthouseAddress) {
        this.renthouseAddress = renthouseAddress;
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

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDateTime getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDateTime lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public LocalDateTime getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDateTime nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 