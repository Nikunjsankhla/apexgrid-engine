package com.apexgrid.engine.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TeamResponseDTO {
    private Long id;
    private String teamName;
    private String captainEmail;
    private Long tournamentId;
    private List<PlayerSummaryDTO> players;

    @Getter
    @Builder
    public static class PlayerSummaryDTO {
        private Long id;
        private String gamerTag;
        private String inGameRole;
    }
}