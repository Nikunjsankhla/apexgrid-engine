package com.apexgrid.engine.entity;

import com.apexgrid.engine.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchFixture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer roundNumber; // 1 = Quarterfinals, 2 = Semifinals, 3 = Finals, etc.

    @Column(nullable = false)
    private Integer matchNumberInRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_a_id")
    private Team teamA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_b_id")
    private Team teamB;

    private Integer scoreTeamA;
    private Integer scoreTeamB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Team winner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;
}
