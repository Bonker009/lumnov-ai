package com.renthouse.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePaymentRequest {
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Payment month is required")
    private LocalDate paymentMonth;

    @PositiveOrZero(message = "Room fee must be positive or zero")
    private BigDecimal roomFee;

    @PositiveOrZero(message = "Electricity fee must be positive or zero")
    private BigDecimal electricityFee;

    @PositiveOrZero(message = "Water fee must be positive or zero")
    private BigDecimal waterFee;

    @PositiveOrZero(message = "Other charges must be positive or zero")
    private BigDecimal otherCharges;

    private String otherChargesDescription;

    private String qrCodeData;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDate getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(LocalDate paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public BigDecimal getRoomFee() {
        return roomFee;
    }

    public void setRoomFee(BigDecimal roomFee) {
        this.roomFee = roomFee;
    }

    public BigDecimal getElectricityFee() {
        return electricityFee;
    }

    public void setElectricityFee(BigDecimal electricityFee) {
        this.electricityFee = electricityFee;
    }

    public BigDecimal getWaterFee() {
        return waterFee;
    }

    public void setWaterFee(BigDecimal waterFee) {
        this.waterFee = waterFee;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public String getOtherChargesDescription() {
        return otherChargesDescription;
    }

    public void setOtherChargesDescription(String otherChargesDescription) {
        this.otherChargesDescription = otherChargesDescription;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }
}