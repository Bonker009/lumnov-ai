package com.renthouse.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateRoomRequest {
    @Size(max = 20, message = "Room number must not exceed 20 characters")
    private String roomNumber;

    private String description;

    @PositiveOrZero(message = "Monthly rent must be positive or zero")
    private BigDecimal monthlyRent;

    @PositiveOrZero(message = "Deposit must be positive or zero")
    private BigDecimal deposit;

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
}