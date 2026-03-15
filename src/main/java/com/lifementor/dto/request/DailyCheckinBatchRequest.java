package com.lifementor.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class DailyCheckinBatchRequest {

    @NotEmpty(message = "At least one response is required")
    @Valid
    private List<DailyCheckinRequest> responses;

    // Getters and Setters
    public List<DailyCheckinRequest> getResponses() { return responses; }
    public void setResponses(List<DailyCheckinRequest> responses) { this.responses = responses; }
}