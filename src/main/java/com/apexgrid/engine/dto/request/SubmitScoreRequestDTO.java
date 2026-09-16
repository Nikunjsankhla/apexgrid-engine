package com.apexgrid.engine.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitScoreRequestDTO {

    @NotNull(message = "Team A score is required")
    @Min(value = 0, message = "Score cannot be negative")
    private Integer scoreTeamA;

    @NotNull(message = "Team B score is required")
    @Min(value = 0, message = "Score cannot be negative")
    private Integer scoreTeamB;
}

