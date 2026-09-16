package com.apexgrid.engine.dto.response;

import com.apexgrid.engine.enums.MatchStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchResponseDTO {
    private Long matchId;
    private Integer roundNumber;
    private Integer matchNumberInRound;
    private String teamAName;
    private String teamBName;
    private Integer scoreTeamA;
    private Integer scoreTeamB;
    private String winnerName;
    private MatchStatus status;
}
