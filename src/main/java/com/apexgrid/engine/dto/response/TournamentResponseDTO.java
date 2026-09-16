package com.apexgrid.engine.dto.response;

import com.apexgrid.engine.enums.GameType;
import com.apexgrid.engine.enums.TournamentStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TournamentResponseDTO {
    private Long id;
    private String title;
    private GameType gameType;
    private TournamentStatus status;
    private Integer maxTeams;
    private Integer registeredTeamCount;
    private List<String> teamNames;
}
