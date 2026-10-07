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
            throw new IllegalStateException("Bracket has already been generated or tournament is closed.");
        }

        List<Team> registeredTeams = tournament.getRegisteredTeams();
        int teamCount = registeredTeams.size();

        if (teamCount != tournament.getMaxTeams()) {
            throw new IllegalStateException(
                String.format("Cannot generate bracket: %d teams registered, but %d are required.",
                        teamCount, tournament.getMaxTeams())
            );
        }

        if ((teamCount & (teamCount - 1)) != 0 || teamCount < 2) {
            throw new IllegalArgumentException("Registered team count must be a power of two (2, 4, 8, 16).");
        }

        List<Team> shuffledTeams = new ArrayList<>(registeredTeams);
        Collections.shuffle(shuffledTeams);

        int totalRounds = (int) (Math.log(teamCount) / Math.log(2));
        List<MatchFixture> allFixtures = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {
            int matchesInRound = teamCount / (int) Math.pow(2, round);
            for (int matchNum = 1; matchNum <= matchesInRound; matchNum++) {
                MatchFixture fixture = MatchFixture.builder()
                        .tournament(tournament)
                        .roundNumber(round)
                        .matchNumberInRound(matchNum)
                        .status(MatchStatus.SCHEDULED)
                        .build();

                if (round == 1) {
                    fixture.setTeamA(shuffledTeams.get((matchNum - 1) * 2));
                    fixture.setTeamB(shuffledTeams.get((matchNum - 1) * 2 + 1));
                }

                allFixtures.add(fixture);
            }
        }

        List<MatchFixture> savedFixtures = matchFixtureRepository.saveAll(allFixtures);
        tournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentRepository.save(tournament);

        return savedFixtures.stream().map(this::mapToMatchResponse).toList();
    }

    @Transactional
    public MatchResponseDTO submitScore(Long matchId, SubmitScoreRequestDTO request) {
        if (request.getScoreTeamA() == request.getScoreTeamB()) {
            throw new IllegalArgumentException("Single-elimination matches cannot end in a draw. One team must win.");
        }

        if (request.getScoreTeamA() < 0 || request.getScoreTeamB() < 0) {
            throw new IllegalArgumentException("Scores cannot be negative values.");
        }

        MatchFixture match = matchFixtureRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        if (match.getStatus() == MatchStatus.COMPLETED) {
            throw new IllegalStateException("Match score has already been submitted and completed.");
        }

        if (match.getTeamA() == null || match.getTeamB() == null) {
            throw new IllegalStateException("Cannot submit scores for an unresolved match.");
        }

        match.setScoreTeamA(request.getScoreTeamA());
        match.setScoreTeamB(request.getScoreTeamB());

        Team winner = (request.getScoreTeamA() > request.getScoreTeamB()) ? match.getTeamA() : match.getTeamB();
        match.setWinner(winner);
        match.setStatus(MatchStatus.COMPLETED);
        matchFixtureRepository.save(match);

        advanceWinner(match, winner);

        return mapToMatchResponse(match);
    }

    private void advanceWinner(MatchFixture currentMatch, Team winner) {
        int nextRound = currentMatch.getRoundNumber() + 1;
        int nextMatchNum = (int) Math.ceil(currentMatch.getMatchNumberInRound() / 2.0);

        var nextMatchOpt = matchFixtureRepository
                .findByTournamentIdAndRoundNumberAndMatchNumberInRound(
                        currentMatch.getTournament().getId(),
                        nextRound,
                        nextMatchNum
                );

        if (nextMatchOpt.isPresent()) {
            MatchFixture nextMatch = nextMatchOpt.get();
            if (currentMatch.getMatchNumberInRound() % 2 != 0) {
                nextMatch.setTeamA(winner);
            } else {
                nextMatch.setTeamB(winner);
            }

            matchFixtureRepository.save(nextMatch);
        } else {
            Tournament tournament = currentMatch.getTournament();
            tournament.setStatus(TournamentStatus.COMPLETED);
            tournamentRepository.save(tournament);
        }
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> getTournamentMatches(Long tournamentId) {
        return matchFixtureRepository
                .findByTournamentIdOrderByRoundNumberAscMatchNumberInRoundAsc(tournamentId)
                .stream()
                .map(this::mapToMatchResponse)
                .toList();
    }

    private MatchResponseDTO mapToMatchResponse(MatchFixture fixture) {
        return MatchResponseDTO.builder()
                .matchId(fixture.getId())
                .roundNumber(fixture.getRoundNumber())
                .matchNumberInRound(fixture.getMatchNumberInRound())
                .status(fixture.getStatus())
                .teamAName(fixture.getTeamA() != null ? fixture.getTeamA().getTeamName() : "TBD")
                .teamBName(fixture.getTeamB() != null ? fixture.getTeamB().getTeamName() : "TBD")
                .scoreTeamA(fixture.getScoreTeamA())
                .scoreTeamB(fixture.getScoreTeamB())
                .winnerName(fixture.getWinner() != null ? fixture.getWinner().getTeamName() : null)
                .build();
    }
}
