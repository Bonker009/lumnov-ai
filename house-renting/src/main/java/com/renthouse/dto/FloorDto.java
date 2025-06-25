package com.renthouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public class FloorDto {
    private Long id;

    @NotNull(message = "Floor number is required")
    @Positive(message = "Floor number must be positive")
    private Integer floorNumber;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long renthouseId;
    private String renthouseName;
    
    // Add rooms field to include room data
    private List<RoomDto> rooms;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    
    public List<RoomDto> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomDto> rooms) {
        this.rooms = rooms;
    }
}