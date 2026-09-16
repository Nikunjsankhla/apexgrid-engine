package com.apexgrid.engine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterTeamRequestDTO {

    @NotBlank(message = "Team name is required")
    private String teamName;

    @Email(message = "Valid captain email is required")
    @NotBlank(message = "Captain email cannot be blank")
    private String captainEmail;

    @NotNull(message = "Tournament ID is required")
    private Long tournamentId;

    @NotEmpty(message = "At least one player is required")
    @Valid
    private List<PlayerInputDTO> players;

    @Getter
    @Setter
    public static class PlayerInputDTO {
        @NotBlank(message = "Gamer tag is required")
        private String gamerTag;

        @NotBlank(message = "Role is required")
        private String inGameRole;
    }
}

