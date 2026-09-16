package com.apexgrid.engine.entity;

import com.apexgrid.engine.enums.GameType;
import com.apexgrid.engine.enums.TournamentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    @Column(nullable = false)
    private Integer maxTeams; // e.g., 4, 8, 16

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Team> registeredTeams = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MatchFixture> matches = new ArrayList<>();
}
