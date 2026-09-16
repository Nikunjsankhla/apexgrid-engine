package com.apexgrid.engine.service;

import com.apexgrid.engine.dto.request.CreateTournamentRequestDTO;
import com.apexgrid.engine.dto.response.TournamentResponseDTO;
import com.apexgrid.engine.entity.Team;
import com.apexgrid.engine.entity.Tournament;
import com.apexgrid.engine.enums.TournamentStatus;
import com.apexgrid.engine.exception.ResourceNotFoundException;
import com.apexgrid.engine.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    @Transactional
    public TournamentResponseDTO createTournament(CreateTournamentRequestDTO request) {
        Tournament tournament = Tournament.builder()
                .title(request.getTitle())
                .gameType(request.getGameType())
                .maxTeams(request.getMaxTeams())
                .status(TournamentStatus.REGISTRATION_OPEN)
                .build();

        Tournament saved = tournamentRepository.save(tournament);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public TournamentResponseDTO getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with ID: " + id));
        return mapToResponse(tournament);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getAllTournaments() {
        return tournamentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TournamentResponseDTO mapToResponse(Tournament t) {
        List<String> teamNames = t.getRegisteredTeams() != null
                ? t.getRegisteredTeams().stream().map(Team::getTeamName).toList()
                : List.of();

        return TournamentResponseDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .gameType(t.getGameType())
                .status(t.getStatus())
                .maxTeams(t.getMaxTeams())
                .registeredTeamCount(teamNames.size())
                .teamNames(teamNames)
                .build();
    }
}
