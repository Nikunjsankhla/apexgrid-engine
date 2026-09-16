package com.apexgrid.engine.service;

import com.apexgrid.engine.dto.request.RegisterTeamRequestDTO;
import com.apexgrid.engine.dto.response.TeamResponseDTO;
import com.apexgrid.engine.entity.Player;
import com.apexgrid.engine.entity.Team;
import com.apexgrid.engine.entity.Tournament;
import com.apexgrid.engine.enums.TournamentStatus;
import com.apexgrid.engine.exception.ResourceNotFoundException;
import com.apexgrid.engine.repository.TeamRepository;
import com.apexgrid.engine.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    @Transactional
    public TeamResponseDTO registerTeam(RegisterTeamRequestDTO request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with ID: " + request.getTournamentId()));

        if (tournament.getStatus() != TournamentStatus.REGISTRATION_OPEN) {
            throw new IllegalStateException("Registration is closed for this tournament");
        }

        if (tournament.getRegisteredTeams().size() >= tournament.getMaxTeams()) {
            throw new IllegalStateException("Tournament team capacity reached");
        }

        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw new IllegalStateException("Team name already taken: " + request.getTeamName());
        }

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .captainEmail(request.getCaptainEmail())
                .tournament(tournament)
                .players(new ArrayList<>())
                .build();

        for (RegisterTeamRequestDTO.PlayerInputDTO playerInput : request.getPlayers()) {
            Player player = Player.builder()
                    .gamerTag(playerInput.getGamerTag())
                    .inGameRole(playerInput.getInGameRole())
                    .team(team)
                    .build();
            team.getPlayers().add(player);
        }

        Team savedTeam = teamRepository.save(team);
        tournament.getRegisteredTeams().add(savedTeam);

        return mapToResponse(savedTeam);
    }

    @Transactional(readOnly = true)
    public TeamResponseDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        return mapToResponse(team);
    }

    private TeamResponseDTO mapToResponse(Team team) {
        List<TeamResponseDTO.PlayerSummaryDTO> players = team.getPlayers().stream()
                .map(p -> TeamResponseDTO.PlayerSummaryDTO.builder()
                        .id(p.getId())
                        .gamerTag(p.getGamerTag())
                        .inGameRole(p.getInGameRole())
                        .build())
                .toList();

        return TeamResponseDTO.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .captainEmail(team.getCaptainEmail())
                .tournamentId(team.getTournament() != null ? team.getTournament().getId() : null)
                .players(players)
                .build();
    }
}
