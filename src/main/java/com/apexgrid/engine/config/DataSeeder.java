package com.apexgrid.engine.config;

import com.apexgrid.engine.entity.Player;
import com.apexgrid.engine.entity.Team;
import com.apexgrid.engine.entity.Tournament;
import com.apexgrid.engine.enums.GameType;
import com.apexgrid.engine.enums.TournamentStatus;
import com.apexgrid.engine.repository.TeamRepository;
import com.apexgrid.engine.repository.TournamentRepository;
import com.apexgrid.engine.service.BracketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final BracketService bracketService;

    @Override
    public void run(String... args) {
        if (tournamentRepository.count() > 0) {
            log.info("Database already contains tournament data. Skipping seeder.");
            return;
        }

        log.info("Seeding demo tournament data for ApexGrid...");

        Tournament tournament = Tournament.builder()
                .title("Apex Masters 2026")
                .gameType(GameType.VALORANT)
                .maxTeams(4)
                .status(TournamentStatus.REGISTRATION_OPEN)
                .registeredTeams(new ArrayList<>())
                .build();
        tournament = tournamentRepository.save(tournament);

        String[] teamNames = {"Sentinels", "Fnatic", "Paper Rex", "Team Liquid"};
        List<Team> createdTeams = new ArrayList<>();

        for (String tName : teamNames) {
            Team team = Team.builder()
                    .teamName(tName)
                    .captainEmail(tName.toLowerCase().replaceAll("\\s+", "") + "@apexgrid.io")
                    .tournament(tournament)
                    .players(new ArrayList<>())
                    .build();

            Player captain = Player.builder()
                    .gamerTag(tName + "_Cap")
                    .inGameRole("CAPTAIN")
                    .team(team)
                    .build();

            team.getPlayers().add(captain);
            team = teamRepository.save(team);
            createdTeams.add(team);
        }

        tournament.setRegisteredTeams(createdTeams);
        tournamentRepository.save(tournament);

        bracketService.generateBracket(tournament.getId());
        log.info("Demo tournament created with ID: {}. Single-elimination bracket generated!", tournament.getId());
    }
}
