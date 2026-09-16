package com.apexgrid.engine.controller;

import com.apexgrid.engine.dto.request.CreateTournamentRequestDTO;
import com.apexgrid.engine.dto.response.TournamentResponseDTO;
import com.apexgrid.engine.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentResponseDTO createTournament(@Valid @RequestBody CreateTournamentRequestDTO request) {
        return tournamentService.createTournament(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TournamentResponseDTO getTournament(@PathVariable Long id) {
        return tournamentService.getTournamentById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TournamentResponseDTO> getAllTournaments() {
        return tournamentService.getAllTournaments();
    }
}
