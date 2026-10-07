package com.apexgrid.engine.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamStandingDTO {
    private Long teamId;
    private String teamName;
    private int matchesPlayed;
    private int matchesWon;
    private int matchesLost;
    private int totalRoundsWon;
    private double winRatePercentage;
}
