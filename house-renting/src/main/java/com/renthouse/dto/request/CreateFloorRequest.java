package com.renthouse.dto.request;

import jakarta.validation.constraints.Positive;

public class CreateFloorRequest {
    @Positive(message = "Floor number must be positive")
    private Integer floorNumber;

    private String description;

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
}