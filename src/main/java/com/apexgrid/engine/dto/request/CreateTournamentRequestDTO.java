package com.apexgrid.engine.dto.request;

import com.apexgrid.engine.enums.GameType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTournamentRequestDTO {

    @NotBlank(message = "Tournament title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    private String title;

    @NotNull(message = "Game type is required")
    private GameType gameType;

    @NotNull(message = "Max teams is required")
    @Min(value = 2, message = "Tournament must have at least 2 teams")
    private Integer maxTeams;
}

    

