package com.apexgrid.engine.controller;

import com.apexgrid.engine.dto.request.SubmitScoreRequestDTO;
import com.apexgrid.engine.dto.response.MatchResponseDTO;
import com.apexgrid.engine.service.BracketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brackets")
@RequiredArgsConstructor
public class BracketController {

    private final BracketService bracketService;

    @PostMapping("/tournaments/{tournamentId}/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MatchResponseDTO> generateBracket(@PathVariable Long tournamentId) {
        return bracketService.generateBracket(tournamentId);
    }

    @GetMapping("/tournaments/{tournamentId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public List<MatchResponseDTO> getTournamentMatches(@PathVariable Long tournamentId) {
        return bracketService.getTournamentMatches(tournamentId);
    }

    @PutMapping("/matches/{matchId}/score")
    @ResponseStatus(HttpStatus.OK)
    public MatchResponseDTO submitScore(
            @PathVariable Long matchId,
            @Valid @RequestBody SubmitScoreRequestDTO request) {
        return bracketService.submitScore(matchId, request);
    }
}
