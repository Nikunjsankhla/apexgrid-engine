package com.apexgrid.engine.service;

import com.apexgrid.engine.dto.request.SubmitScoreRequestDTO;
import com.apexgrid.engine.dto.response.MatchResponseDTO;
import com.apexgrid.engine.entity.MatchFixture;
import com.apexgrid.engine.entity.Team;
import com.apexgrid.engine.entity.Tournament;
import com.apexgrid.engine.enums.MatchStatus;
import com.apexgrid.engine.enums.TournamentStatus;
import com.apexgrid.engine.exception.ResourceNotFoundException;
import com.apexgrid.engine.repository.MatchFixtureRepository;
import com.apexgrid.engine.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BracketService {

    private final TournamentRepository tournamentRepository;
    private final MatchFixtureRepository matchFixtureRepository;

    @Transactional
    public List<MatchResponseDTO> generateBracket(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with ID: " + tournamentId));

        if (tournament.getStatus() != TournamentStatus.REGISTRATION_OPEN) {
            throw new IllegalStateException("Bracket has already been generated or tournament is inactive");
        }

        List<Team> teams = new ArrayList<>(tournament.getRegisteredTeams());
        if (teams.size() < 2) {
            throw new IllegalStateException("At least 2 teams required to generate a bracket");
        }

        Collections.shuffle(teams);

        int totalTeams = teams.size();
        int round1Matches = totalTeams / 2;
        List<MatchFixture> allFixtures = new ArrayList<>();

        // Generate Round 1 Fixtures
        for (int i = 0; i < round1Matches; i++) {
            MatchFixture fixture = MatchFixture.builder()
                    .tournament(tournament)
                    .roundNumber(1)
                    .matchNumberInRound(i + 1)
                    .teamA(teams.get(i * 2))
                    .teamB(teams.get(i * 2 + 1))
                    .status(MatchStatus.SCHEDULED)
                    .build();
            allFixtures.add(fixture);
        }

        // Pre-create empty placeholder slots for subsequent rounds
        int currentRoundTeams = round1Matches;
        int roundNumber = 2;
        while (currentRoundTeams > 1) {
            int nextRoundMatches = currentRoundTeams / 2;
            for (int i = 0; i < nextRoundMatches; i++) {
                MatchFixture placeholder = MatchFixture.builder()
                        .tournament(tournament)
                        .roundNumber(roundNumber)
                        .matchNumberInRound(i + 1)
                        .status(MatchStatus.SCHEDULED)
                        .build();
                allFixtures.add(placeholder);
            }
            currentRoundTeams = nextRoundMatches;
            roundNumber++;
        }

        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentRepository.save(tournament);
        List<MatchFixture> savedFixtures = matchFixtureRepository.saveAll(allFixtures);

        return savedFixtures.stream().map(this::mapToMatchResponse).toList();
    }

    @Transactional
    public MatchResponseDTO submitScore(Long matchId, SubmitScoreRequestDTO request) {
        MatchFixture match = matchFixtureRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match fixture not found with ID: " + matchId));

        if (match.getTeamA() == null || match.getTeamB() == null) {
            throw new IllegalStateException("Both teams must be resolved before submitting scores");
        }

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Match score has already been submitted");
        }

        if (request.getScoreTeamA().equals(request.getScoreTeamB())) {
            throw new IllegalStateException("Draws not supported; tournament matches require an outright winner");
        }

        match.setScoreTeamA(request.getScoreTeamA());
        match.setScoreTeamB(request.getScoreTeamB());
        match.setStatus(MatchStatus.COMPLETED);

        Team winner = request.getScoreTeamA() > request.getScoreTeamB() ? match.getTeamA() : match.getTeamB();
        match.setWinner(winner);
        matchFixtureRepository.save(match);

        advanceWinnerToNextRound(match, winner);

        return mapToMatchResponse(match);
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> getTournamentMatches(Long tournamentId) {
        return matchFixtureRepository.findByTournamentIdOrderByRoundNumberAscMatchNumberInRoundAsc(tournamentId)
                .stream()
                .map(this::mapToMatchResponse)
                .toList();
    }

    private void advanceWinnerToNextRound(MatchFixture currentMatch, Team winner) {
        int nextRoundNumber = currentMatch.getRoundNumber() + 1;
        int nextMatchNumber = (int) Math.ceil(currentMatch.getMatchNumberInRound() / 2.0);

        List<MatchFixture> matches = matchFixtureRepository
                .findByTournamentIdOrderByRoundNumberAscMatchNumberInRoundAsc(currentMatch.getTournament().getId());

        MatchFixture nextMatch = matches.stream()
                .filter(m -> m.getRoundNumber().equals(nextRoundNumber) && m.getMatchNumberInRound().equals(nextMatchNumber))
                .findFirst()
                .orElse(null);

        if (nextMatch != null) {
            // If current match index was odd -> fills Team A, if even -> fills Team B
            if (currentMatch.getMatchNumberInRound() % 2 != 0) {
                nextMatch.setTeamA(winner);
            } else {
                nextMatch.setTeamB(winner);
            }
            matchFixtureRepository.save(nextMatch);
        } else {
            // Reached the pinnacle round -> this was the final!
            Tournament tournament = currentMatch.getTournament();
            tournament.setStatus(TournamentStatus.COMPLETED);
            tournamentRepository.save(tournament);
        }
    }

    private MatchResponseDTO mapToMatchResponse(MatchFixture m) {
        return MatchResponseDTO.builder()
                .matchId(m.getId())
                .roundNumber(m.getRoundNumber())
                .matchNumberInRound(m.getMatchNumberInRound())
                .teamAName(m.getTeamA() != null ? m.getTeamA().getTeamName() : "TBD")
                .teamBName(m.getTeamB() != null ? m.getTeamB().getTeamName() : "TBD")
                .scoreTeamA(m.getScoreTeamA())
                .scoreTeamB(m.getScoreTeamB())
                .winnerName(m.getWinner() != null ? m.getWinner().getTeamName() : null)
                .status(m.getStatus())
                .build();
    }
}
